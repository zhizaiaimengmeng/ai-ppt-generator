package com.pptai.controller;

import com.pptai.dto.ApiResponse;
import com.pptai.dto.common.PageResponse;
import com.pptai.dto.template.TemplateCreateRequest;
import com.pptai.dto.template.TemplateLayout;
import com.pptai.dto.template.TemplateResponse;
import com.pptai.dto.user.UserResponse;
import com.pptai.service.TemplateDesignerService;
import com.pptai.service.TemplateService;
import com.pptai.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {
    
    private final TemplateService templateService;
    private final UserService userService;
    private final TemplateDesignerService templateDesignerService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TemplateResponse>>> getTemplates(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        var pageable = org.springframework.data.domain.PageRequest.of(page - 1, size);
        var response = templateService.getTemplates(category, keyword, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TemplateResponse>> getTemplate(@PathVariable Long id) {
        TemplateResponse template = templateService.getTemplateById(id);
        return ResponseEntity.ok(ApiResponse.success(template));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createTemplate(
            @Valid @RequestBody TemplateCreateRequest request) {
        templateService.createTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("模板创建成功", null));
    }
    
    @PostMapping("/{id}/favorite")
    public ResponseEntity<ApiResponse<Void>> favoriteTemplate(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        templateService.favoriteTemplate(id, user.getId());
        
        return ResponseEntity.ok(ApiResponse.success("收藏成功", null));
    }
    
    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<ApiResponse<Void>> unfavoriteTemplate(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        templateService.unfavoriteTemplate(id, user.getId());
        
        return ResponseEntity.ok(ApiResponse.success("取消收藏成功", null));
    }
    
    /**
     * 获取预设模板布局
     */
    @GetMapping("/layouts/{style}")
    public ResponseEntity<ApiResponse<List<TemplateLayout>>> getTemplateLayouts(
            @PathVariable String style) {
        
        List<TemplateLayout> layouts = switch (style.toLowerCase()) {
            case "business" -> templateDesignerService.generateBusinessTemplate();
            case "tech", "technology" -> templateDesignerService.generateTechTemplate();
            case "education" -> templateDesignerService.generateEducationTemplate();
            case "minimal", "simple" -> templateDesignerService.generateMinimalTemplate();
            default -> throw new IllegalArgumentException("不支持的模板风格：" + style);
        };
        
        return ResponseEntity.ok(ApiResponse.success(layouts));
    }
    
    /**
     * 获取所有可用模板风格
     */
    @GetMapping("/styles")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getTemplateStyles() {
        List<Map<String, String>> styles = List.of(
            Map.of("id", "business", "name", "商务风", "description", "专业、稳重的商务风格"),
            Map.of("id", "tech", "name", "科技感", "description", "现代、炫酷的科技风格"),
            Map.of("id", "education", "name", "教育风", "description", "清新、活泼的教育风格"),
            Map.of("id", "minimal", "name", "简约风", "description", "简洁、大气的简约风格")
        );
        
        return ResponseEntity.ok(ApiResponse.success(styles));
    }
    
    /**
     * 初始化预定义模板
     * 用于系统初始化或重新生成模板
     */
    @PostMapping("/init-presets")
    public ResponseEntity<ApiResponse<Void>> initPresetTemplates() {
        templateService.generatePresetTemplates();
        return ResponseEntity.ok(ApiResponse.success("预定义模板生成成功", null));
    }
}
