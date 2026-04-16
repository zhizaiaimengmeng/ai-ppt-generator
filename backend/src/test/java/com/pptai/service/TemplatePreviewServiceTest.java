package com.pptai.service;

import com.pptai.dto.template.TemplateLayout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TemplatePreviewService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class TemplatePreviewServiceTest {
    
    @InjectMocks
    private TemplatePreviewService templatePreviewService;
    
    @BeforeEach
    void setUp() {
    }
    
    @Test
    @DisplayName("应该成功生成模板预览图")
    void shouldGenerateTemplatePreview() {
        // Given
        List<TemplateLayout> layouts = List.of(
            TemplateLayout.builder()
                .layoutType("title")
                .name("测试布局")
                .backgroundColor("#667eea")
                .build()
        );
        
        // When
        String previewBase64 = templatePreviewService.generatePreview(layouts);
        
        // Then
        assertNotNull(previewBase64);
        assertTrue(previewBase64.length() > 0);
    }
    
    @Test
    @DisplayName("当布局列表为空时应生成默认预览图")
    void shouldGenerateDefaultPreviewWhenEmptyList() {
        // When
        String previewBase64 = templatePreviewService.generatePreview(null);
        
        // Then
        assertNotNull(previewBase64);
        assertTrue(previewBase64.length() > 0);
    }
    
    @Test
    @DisplayName("应该成功生成带渐变的预览图")
    void shouldGeneratePreviewWithGradient() {
        // Given
        TemplateLayout.GradientConfig gradient = TemplateLayout.GradientConfig.builder()
            .type("linear")
            .direction("45deg")
            .colors(List.of("#3a7bd5", "#00d2ff"))
            .build();
        
        List<TemplateLayout> layouts = List.of(
            TemplateLayout.builder()
                .layoutType("title")
                .name("渐变布局")
                .gradient(gradient)
                .build()
        );
        
        // When
        String previewBase64 = templatePreviewService.generatePreview(layouts);
        
        // Then
        assertNotNull(previewBase64);
        assertTrue(previewBase64.length() > 0);
    }
    
    @Test
    @DisplayName("应该成功生成单个幻灯片布局预览图")
    void shouldGenerateSlidePreview() {
        // Given
        TemplateLayout layout = TemplateLayout.builder()
            .layoutType("content")
            .name("内容页")
            .backgroundColor("#ffffff")
            .elements(List.of(
                TemplateLayout.LayoutElement.builder()
                    .type("title")
                    .position("top")
                    .fontSize(28)
                    .color("#333333")
                    .build()
            ))
            .build();
        
        // When
        String previewBase64 = templatePreviewService.generateSlidePreview(layout);
        
        // Then
        assertNotNull(previewBase64);
        assertTrue(previewBase64.length() > 0);
    }
    
    @Test
    @DisplayName("预览图应该是有效的 Base64 格式")
    void previewShouldBeValidBase64() {
        // Given
        TemplateLayout layout = TemplateLayout.builder()
            .layoutType("title")
            .name("测试")
            .backgroundColor("#667eea")
            .build();
        
        // When
        String previewBase64 = templatePreviewService.generatePreview(List.of(layout));
        
        // Then
        assertNotNull(previewBase64);
        
        // 验证 Base64 格式
        assertDoesNotThrow(() -> {
            java.util.Base64.getDecoder().decode(previewBase64);
        });
    }
}
