package com.pptai.service;

import com.pptai.dto.auth.AuthResponse;
import com.pptai.dto.auth.LoginRequest;
import com.pptai.dto.auth.RegisterRequest;
import com.pptai.entity.User;
import com.pptai.exception.BusinessException;
import com.pptai.repository.UserRepository;
import com.pptai.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private CustomUserDetailsService userDetailsService;
    
    @Mock
    private EmailService emailService;
    
    @Mock
    private UserDetails userDetails;
    
    @InjectMocks
    private AuthService authService;
    
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");
        
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("encodedPassword");
        testUser.setEmailVerified(false);
    }
    
    @Test
    @DisplayName("用户注册 - 成功场景")
    void testRegister_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(any(UserDetails.class))).thenReturn("token");
        when(jwtTokenProvider.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");
        
        // When
        AuthResponse response = authService.register(registerRequest);
        
        // Then
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getRefreshToken());
        assertNotNull(response.getUser());
        assertEquals("test@example.com", response.getUser().getEmail());
        
        verify(userRepository).save(any(User.class));
        verify(emailService).sendVerificationEmail(any(User.class));
    }
    
    @Test
    @DisplayName("用户注册 - 邮箱已存在")
    void testRegister_EmailExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        
        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> authService.register(registerRequest)
        );
        
        assertEquals("邮箱已被注册", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    @DisplayName("用户注册 - 密码不匹配")
    void testRegister_PasswordMismatch() {
        // Given
        registerRequest.setConfirmPassword("differentPassword");
        
        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> authService.register(registerRequest)
        );
        
        assertEquals("两次输入的密码不一致", exception.getMessage());
    }
    
    @Test
    @DisplayName("用户登录 - 成功场景")
    void testLogin_Success() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(any(UserDetails.class))).thenReturn("token");
        when(jwtTokenProvider.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");
        
        // When
        AuthResponse response = authService.login(loginRequest);
        
        // Then
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getRefreshToken());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
    
    @Test
    @DisplayName("用户登录 - 凭证错误")
    void testLogin_InvalidCredentials() {
        // Given
        doThrow(new BadCredentialsException("Bad credentials"))
            .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        
        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> authService.login(loginRequest)
        );
        
        assertEquals("邮箱或密码错误", exception.getMessage());
    }
    
    @Test
    @DisplayName("邮箱验证 - 成功场景")
    void testVerifyEmail_Success() {
        // Given
        String token = "verificationToken";
        testUser.setVerificationToken(token);
        
        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        authService.verifyEmail(token);
        
        // Then
        assertTrue(testUser.getEmailVerified());
        assertNull(testUser.getVerificationToken());
        verify(userRepository).save(testUser);
    }
    
    @Test
    @DisplayName("重置密码 - 成功场景")
    void testResetPassword_Success() {
        // Given
        String token = "resetToken";
        String newPassword = "newPassword123";
        
        testUser.setResetPasswordToken(token);
        testUser.setResetPasswordExpires(java.time.LocalDateTime.now().plusHours(24));
        
        when(userRepository.findByResetPasswordToken(token)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        authService.resetPassword(token, newPassword);
        
        // Then
        assertEquals("encodedNewPassword", testUser.getPasswordHash());
        assertNull(testUser.getResetPasswordToken());
        verify(userRepository).save(testUser);
    }
}
