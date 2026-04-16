package com.pptai.controller;

import com.pptai.dto.ApiResponse;
import com.pptai.dto.ppt.GenerationRequest;
import com.pptai.dto.ppt.GenerationResponse;
import com.pptai.dto.user.UserResponse;
import com.pptai.service.PPTGenerationService;
import com.pptai.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ppt")
@RequiredArgsConstructor
public class PPTController {
    
    private final PPTGenerationService pptGenerationService;
    private final UserService userService;
    
    /**
     * 生成 PPT
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<GenerationResponse>> generatePPT(
            @Valid @RequestBody GenerationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        
        // 异步生成
        pptGenerationService.generateAsync(request, user.getId());
        
        GenerationResponse response = GenerationResponse.builder()
            .status("pending")
            .estimatedTime(60)
            .build();
        
        return ResponseEntity.ok(ApiResponse.success("开始生成，请稍后查看进度", response));
    }
    
    /**
     * 获取生成进度
     */
    @GetMapping("/generation/{projectId}/status")
    public ResponseEntity<ApiResponse<GenerationResponse>> getGenerationStatus(
            @PathVariable Long projectId) {
        
        GenerationResponse response = pptGenerationService.getGenerationStatus(projectId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
