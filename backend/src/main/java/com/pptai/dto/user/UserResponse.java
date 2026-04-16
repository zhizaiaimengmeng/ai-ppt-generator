package com.pptai.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    
    private String email;
    
    private String nickname;
    
    private String avatarUrl;
    
    private Boolean emailVerified;
    
    private LocalDateTime createdAt;
}
