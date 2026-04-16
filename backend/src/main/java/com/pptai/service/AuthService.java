package com.pptai.service;

import com.pptai.dto.auth.AuthResponse;
import com.pptai.dto.auth.LoginRequest;
import com.pptai.dto.auth.RegisterRequest;
import com.pptai.dto.user.UserResponse;
import com.pptai.entity.User;
import com.pptai.exception.BusinessException;
import com.pptai.exception.ErrorCode;
import com.pptai.repository.UserRepository;
import com.pptai.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final EmailService emailService;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 验证密码匹配
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PARAMS.getCode(), 
                "两次输入的密码不一致");
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL.getCode(),
                "邮箱已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getEmail().split("@")[0]);
        user.setEmailVerified(false);
        user.setVerificationToken(UUID.randomUUID().toString());
        
        userRepository.save(user);
        log.info("用户注册成功：{}", user.getEmail());
        
        // 发送验证邮件
        emailService.sendVerificationEmail(user);
        
        // 生成 token
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtTokenProvider.generateToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
        
        return AuthResponse.builder()
            .token(token)
            .refreshToken(refreshToken)
            .user(toUserResponse(user))
            .build();
    }
    
    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
        } catch (Exception e) {
            log.error("登录失败：{}", e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS.getCode(),
                "邮箱或密码错误");
        }
        
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(),
                "用户不存在"));
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtTokenProvider.generateToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
        
        log.info("用户登录成功：{}", user.getEmail());
        
        return AuthResponse.builder()
            .token(token)
            .refreshToken(refreshToken)
            .user(toUserResponse(user))
            .build();
    }
    
    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
            .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED.getCode(),
                "无效的验证令牌"));
        
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        
        log.info("邮箱验证成功：{}", user.getEmail());
    }
    
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(),
                "用户不存在"));
        
        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordExpires(LocalDateTime.now().plusHours(24));
        userRepository.save(user);
        
        emailService.sendPasswordResetEmail(user, resetToken);
        
        log.info("密码重置邮件已发送：{}", email);
    }
    
    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
            .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED.getCode(),
                "无效的重置令牌"));
        
        if (user.getResetPasswordExpires() == null || 
            user.getResetPasswordExpires().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED.getCode(),
                "重置令牌已过期");
        }
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpires(null);
        userRepository.save(user);
        
        log.info("密码重置成功：{}", user.getEmail());
    }
    
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(),
                "用户不存在"));
        
        return toUserResponse(user);
    }
    
    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .avatarUrl(user.getAvatarUrl())
            .emailVerified(user.getEmailVerified())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
