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
 * TemplateDesignerService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class TemplateDesignerServiceTest {
    
    @Mock
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    
    @InjectMocks
    private TemplateDesignerService templateDesignerService;
    
    @BeforeEach
    void setUp() {
    }
    
    @Test
    @DisplayName("应该成功生成商务模板布局")
    void shouldGenerateBusinessTemplate() {
        // Given
        
        // When
        List<TemplateLayout> layouts = templateDesignerService.generateBusinessTemplate();
        
        // Then
        assertNotNull(layouts);
        assertEquals(6, layouts.size());
        
        // 验证封面页
        TemplateLayout titleLayout = layouts.get(0);
        assertEquals("title", titleLayout.getLayoutType());
        assertEquals("封面页", titleLayout.getName());
        assertEquals("#667eea", titleLayout.getBackgroundColor());
        assertNotNull(titleLayout.getElements());
        assertEquals(2, titleLayout.getElements().size());
        
        // 验证结束页
        TemplateLayout endLayout = layouts.get(5);
        assertEquals("end", endLayout.getLayoutType());
        assertEquals("结束页", endLayout.getName());
        assertEquals("#764ba2", endLayout.getBackgroundColor());
    }
    
    @Test
    @DisplayName("应该成功生成科技模板布局")
    void shouldGenerateTechTemplate() {
        // When
        List<TemplateLayout> layouts = templateDesignerService.generateTechTemplate();
        
        // Then
        assertNotNull(layouts);
        assertTrue(layouts.size() >= 1);
        
        // 验证科技封面
        TemplateLayout techTitle = layouts.get(0);
        assertEquals("title", techTitle.getLayoutType());
        assertEquals("科技封面", techTitle.getName());
        assertEquals("#0f0c29", techTitle.getBackgroundColor());
        
        // 验证渐变配置
        assertNotNull(techTitle.getGradient());
        assertEquals("linear", techTitle.getGradient().getType());
        assertTrue(techTitle.getGradient().getColors().size() >= 2);
    }
    
    @Test
    @DisplayName("应该成功生成教育模板布局")
    void shouldGenerateEducationTemplate() {
        // When
        List<TemplateLayout> layouts = templateDesignerService.generateEducationTemplate();
        
        // Then
        assertNotNull(layouts);
        assertTrue(layouts.size() >= 1);
        
        // 验证教育封面
        TemplateLayout eduTitle = layouts.get(0);
        assertEquals("title", eduTitle.getLayoutType());
        assertEquals("教育封面", eduTitle.getName());
        
        // 验证渐变配置
        assertNotNull(eduTitle.getGradient());
        assertEquals("linear", eduTitle.getGradient().getType());
        assertEquals("to right", eduTitle.getGradient().getDirection());
    }
    
    @Test
    @DisplayName("应该成功生成简约模板布局")
    void shouldGenerateMinimalTemplate() {
        // When
        List<TemplateLayout> layouts = templateDesignerService.generateMinimalTemplate();
        
        // Then
        assertNotNull(layouts);
        assertTrue(layouts.size() >= 1);
        
        // 验证简约封面
        TemplateLayout minimalTitle = layouts.get(0);
        assertEquals("title", minimalTitle.getLayoutType());
        assertEquals("简约封面", minimalTitle.getName());
        assertEquals("#ffffff", minimalTitle.getBackgroundColor());
    }
    
    @Test
    @DisplayName("商务模板布局应该包含必需的占位符键")
    void businessTemplateShouldHavePlaceholderKeys() {
        // When
        List<TemplateLayout> layouts = templateDesignerService.generateBusinessTemplate();
        
        // Then
        TemplateLayout contentLayout = layouts.get(2); // 内容页
        assertNotNull(contentLayout.getElements());
        
        boolean hasTitlePlaceholder = contentLayout.getElements().stream()
            .anyMatch(e -> "title".equals(e.getType()) && e.getPlaceholderKey() != null);
        assertTrue(hasTitlePlaceholder);
        
        boolean hasContentPlaceholder = contentLayout.getElements().stream()
            .anyMatch(e -> "content".equals(e.getType()) && e.getPlaceholderKey() != null);
        assertTrue(hasContentPlaceholder);
    }
    
    @Test
    @DisplayName("所有模板元素应该包含必需字段")
    void allTemplateElementsShouldHaveRequiredFields() {
        // When
        List<TemplateLayout> businessLayouts = templateDesignerService.generateBusinessTemplate();
        List<TemplateLayout> techLayouts = templateDesignerService.generateTechTemplate();
        List<TemplateLayout> educationLayouts = templateDesignerService.generateEducationTemplate();
        List<TemplateLayout> minimalLayouts = templateDesignerService.generateMinimalTemplate();
        
        // Then
        assertAllElementsValid(businessLayouts);
        assertAllElementsValid(techLayouts);
        assertAllElementsValid(educationLayouts);
        assertAllElementsValid(minimalLayouts);
    }
    
    private void assertAllElementsValid(List<TemplateLayout> layouts) {
        for (TemplateLayout layout : layouts) {
            assertNotNull(layout.getLayoutType(), "Layout type should not be null");
            assertNotNull(layout.getName(), "Layout name should not be null");
            assertNotNull(layout.getElements(), "Elements should not be null");
            
            for (TemplateLayout.LayoutElement element : layout.getElements()) {
                assertNotNull(element.getType(), "Element type should not be null");
                assertNotNull(element.getPosition(), "Element position should not be null");
            }
        }
    }
}
