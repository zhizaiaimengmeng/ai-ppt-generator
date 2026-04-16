package com.pptai.controller;

import com.pptai.dto.ApiResponse;
import com.pptai.dto.export.ExportRequest;
import com.pptai.dto.export.ExportResponse;
import com.pptai.dto.export.ShareLinkRequest;
import com.pptai.dto.user.UserResponse;
import com.pptai.service.ExportService;
import com.pptai.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {
    
    private final ExportService exportService;
    private final UserService userService;
    
    /**
     * 导出 PPT
     */
    @PostMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ExportResponse>> exportPPT(
            @PathVariable Long projectId,
            @Valid @RequestBody ExportRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        
        // 异步导出
        exportService.exportAsync(projectId, user.getId(), request.getFormat());
        
        ExportResponse response = ExportResponse.builder()
            .status("pending")
            .progress(0)
            .build();
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("开始导出，请稍后查看进度", response));
    }
    
    /**
     * 获取导出状态
     */
    @GetMapping("/{exportId}/status")
    public ResponseEntity<ApiResponse<ExportResponse>> getExportStatus(
            @PathVariable Long exportId) {
        
        ExportResponse response = exportService.getExportStatus(exportId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 生成分享链接
     */
    @PostMapping("/{projectId}/share-link")
    public ResponseEntity<ApiResponse<ExportResponse>> createShareLink(
            @PathVariable Long projectId,
            @RequestBody ShareLinkRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        
        ExportResponse response = exportService.createShareLink(
            projectId, user.getId(), request);
        
        return ResponseEntity.ok(ApiResponse.success("分享链接生成成功", response));
    }
}
