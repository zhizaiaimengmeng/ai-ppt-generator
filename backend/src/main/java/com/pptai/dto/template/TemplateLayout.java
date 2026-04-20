package com.pptai.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 模板布局定义
 * 用于描述单张幻灯片的布局结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateLayout {
    
    private String layoutType;
    private String name;
    private String backgroundColor;
    private GradientConfig gradient;
    private String backgroundImage;
    private List<LayoutElement> elements;
    private Map<String, Object> placeholders;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GradientConfig {
        private String type;
        private String direction;
        private List<String> colors;
        private List<String> stops;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LayoutElement {
        private String type;
        private String position;
        private Integer fontSize;
        private String color;
        private String fontWeight;
        private String width;
        private String height;
        private String textAlign;
        private String className;
        private Map<String, Object> styles;
        private String placeholderKey;
        private Boolean required;
    }
}
