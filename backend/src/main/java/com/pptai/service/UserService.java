package com.pptai.service;

import com.pptai.dto.user.UserResponse;
import com.pptai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("用户不存在：" + email));
        
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
