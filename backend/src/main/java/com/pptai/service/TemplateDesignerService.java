package com.pptai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pptai.dto.template.TemplateLayout;
import com.pptai.entity.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 模板设计器服务
 * 用于创建和管理 PPT 模板布局
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateDesignerService {
    
    private final ObjectMapper objectMapper;
    
    /**
     * 生成标准商务模板的幻灯片布局
     */
    public List<TemplateLayout> generateBusinessTemplate() {
        List<TemplateLayout> layouts = new ArrayList<>();
        
        // 1. 封面页
        layouts.add(TemplateLayout.builder()
            .layoutType("title")
            .name("封面页")
            .backgroundColor("#667eea")
            .elements(List.of(
                createTitleElement("center", 48, "#ffffff", "title"),
                createSubtitleElement("bottom", 24, "#f0f0f0", "subtitle")
            ))
            .build());
        
        // 2. 目录页
        layouts.add(TemplateLayout.builder()
            .layoutType("content")
            .name("目录页")
            .backgroundColor("#ffffff")
            .elements(List.of(
                createTitleElement("top", 32, "#333333", "title"),
                createListElement("center", 18, "#666666", "outline")
            ))
            .build());
        
        // 3. 内容页
        layouts.add(TemplateLayout.builder()
            .layoutType("content")
            .name("内容页")
            .backgroundColor("#ffffff")
            .elements(List.of(
                createTitleElement("top", 28, "#333333", "title"),
                createContentElement("center", 16, "#666666", "content")
            ))
            .build());
        
        // 4. 图文页
        layouts.add(TemplateLayout.builder()
            .layoutType("image-content")
            .name("图文页")
            .backgroundColor("#f8f9fa")
            .elements(List.of(
                createTitleElement("top", 28, "#333333", "title"),
                createImageElement("left", "50%", "image"),
                createContentElement("right", 16, "#666666", "content")
            ))
            .build());
        
        // 5. 列表页
        layouts.add(TemplateLayout.builder()
            .layoutType("list")
            .name("列表页")
            .backgroundColor("#ffffff")
            .elements(List.of(
                createTitleElement("top", 28, "#333333", "title"),
                createNumberedListElement("center", 18, "#666666", "points")
            ))
            .build());
        
        // 6. 结束页
        layouts.add(TemplateLayout.builder()
            .layoutType("end")
            .name("结束页")
            .backgroundColor("#764ba2")
            .elements(List.of(
                createThankYouElement("center", 48, "#ffffff", "thankYou"),
                createContactElement("bottom", 16, "#f0f0f0", "contact")
            ))
            .build());
        
        log.info("生成商务模板布局，共{}种", layouts.size());
        return layouts;
    }
    
    /**
     * 生成科技风格模板
     */
    public List<TemplateLayout> generateTechTemplate() {
        List<TemplateLayout> layouts = new ArrayList<>();
        
        // 1. 科技封面
        layouts.add(TemplateLayout.builder()
            .layoutType("title")
            .name("科技封面")
            .backgroundColor("#0f0c29")
            .gradient(TemplateLayout.GradientConfig.builder()
                .type("linear")
                .direction("45deg")
                .colors(List.of("#3a7bd5", "#00d2ff"))
                .build())
            .elements(List.of(
                createTitleElement("center", 48, "#00d2ff", "title"),
                createSubtitleElement("bottom", 24, "#ffffff", "subtitle"),
                TemplateLayout.LayoutElement.builder()
                    .type("logo")
                    .position("top-right")
                    .width("80px")
                    .height("80px")
                    .required(false)
                    .build()
            ))
            .build());
        
        // 2. 科技章节页
        layouts.add(TemplateLayout.builder()
            .layoutType("section")
            .name("科技章节页")
            .backgroundColor("#1a1a2e")
            .elements(List.of(
                createTitleElement("center", 40, "#00d2ff", "sectionTitle"),
                TemplateLayout.LayoutElement.builder()
                    .type("icon")
                    .position("top-left")
                    .className("tech-icon")
                    .styles(java.util.Map.of("size", "64px", "color", "#00d2ff"))
                    .required(false)
                    .build()
            ))
            .build());
        
        // 3. 科技内容页
        layouts.add(TemplateLayout.builder()
            .layoutType("content")
            .name("科技内容页")
            .backgroundColor("#16213e")
            .elements(List.of(
                createTitleElement("top", 28, "#00d2ff", "title"),
                createContentElement("center", 16, "#e0e0e0", "content"),
                TemplateLayout.LayoutElement.builder()
                    .type("footer")
                    .position("bottom")
                    .fontSize(12)
                    .color("#888888")
                    .placeholderKey("footer")
                    .required(false)
                    .build()
            ))
            .build());
        
        // 4. 科技数据页
        layouts.add(TemplateLayout.builder()
            .layoutType("image-content")
            .name("科技数据页")
            .backgroundColor("#0f3460")
            .elements(List.of(
                createTitleElement("top", 28, "#00d2ff", "title"),
                createChartElement("left", "50%", "chart"),
                createContentElement("right", 14, "#e0e0e0", "description")
            ))
            .build());
        
        log.info("生成科技模板布局，共{}种", layouts.size());
        return layouts;
    }
    
    /**
     * 生成教育风格模板
     */
    public List<TemplateLayout> generateEducationTemplate() {
        List<TemplateLayout> layouts = new ArrayList<>();
        
        // 1. 教育封面
        layouts.add(TemplateLayout.builder()
            .layoutType("title")
            .name("教育封面")
            .backgroundColor("#56ab2f")
            .gradient(TemplateLayout.GradientConfig.builder()
                .type("linear")
                .direction("to right")
                .colors(List.of("#56ab2f", "#a8e063"))
                .build())
            .elements(List.of(
                createTitleElement("center", 48, "#ffffff", "title"),
                TemplateLayout.LayoutElement.builder()
                    .type("icon")
                    .position("top")
                    .className("education-icon")
                    .styles(java.util.Map.of("size", "80px"))
                    .required(false)
                    .build()
            ))
            .build());
        
        // 2. 课程目录
        layouts.add(TemplateLayout.builder()
            .layoutType("content")
            .name("课程目录")
            .backgroundColor("#ffffff")
            .elements(List.of(
                createTitleElement("top", 32, "#2c3e50", "title"),
                createNumberedListElement("center", 20, "#27ae60", "chapters")
            ))
            .build());
        
        // 3. 知识点页
        layouts.add(TemplateLayout.builder()
            .layoutType("content")
            .name("知识点讲解")
            .backgroundColor("#f9fff9")
            .elements(List.of(
                createTitleElement("top", 28, "#27ae60", "title"),
                createContentElement("center", 18, "#34495e", "content"),
                TemplateLayout.LayoutElement.builder()
                    .type("quote")
                    .position("bottom")
                    .fontSize(14)
                    .color("#7f8c8d")
                    .className("key-point")
                    .placeholderKey("keyPoint")
                    .required(false)
                    .build()
            ))
            .build());
        
        // 4. 图文教学页
        layouts.add(TemplateLayout.builder()
            .layoutType("image-content")
            .name("图文教学")
            .backgroundColor("#ffffff")
            .elements(List.of(
                createTitleElement("top", 28, "#27ae60", "title"),
                createImageElement("right", "45%", "illustration"),
                createContentElement("left", 16, "#34495e", "content")
            ))
            .build());
        
        // 5. 总结页
        layouts.add(TemplateLayout.builder()
            .layoutType("end")
            .name("课程总结")
            .backgroundColor("#a8e063")
            .elements(List.of(
                TemplateLayout.LayoutElement.builder()
                    .type("thank-you")
                    .position("center")
                    .fontSize(42)
                    .color("#ffffff")
                    .placeholderKey("summary")
                    .required(true)
                    .build(),
                TemplateLayout.LayoutElement.builder()
                    .type("contact")
                    .position("bottom")
                    .fontSize(14)
                    .color("#ffffff")
                    .placeholderKey("teacher")
                    .required(false)
                    .build()
            ))
            .build());
        
        log.info("生成教育模板布局，共{}种", layouts.size());
        return layouts;
    }
    
    /**
     * 生成简约风格模板
     */
    public List<TemplateLayout> generateMinimalTemplate() {
        List<TemplateLayout> layouts = new ArrayList<>();
        
        // 1. 简约封面
        layouts.add(TemplateLayout.builder()
            .layoutType("title")
            .name("简约封面")
            .backgroundColor("#ffffff")
            .elements(List.of(
                createTitleElement("center", 52, "#000000", "title"),
                createSubtitleElement("bottom", 22, "#666666", "subtitle")
            ))
            .build());
        
        // 2. 纯文本页
        layouts.add(TemplateLayout.builder()
            .layoutType("content")
            .name("纯文本页")
            .backgroundColor("#ffffff")
            .elements(List.of(
                createTitleElement("top", 26, "#000000", "title"),
                createContentElement("center", 16, "#333333", "content")
            ))
            .build());
        
        // 3. 大图背景页
        layouts.add(TemplateLayout.builder()
            .layoutType("image-content")
            .name("大图展示")
            .backgroundImage("full")
            .elements(List.of(
                createTitleElement("bottom", 32, "#ffffff", "title"),
                TemplateLayout.LayoutElement.builder()
                    .type("overlay")
                    .position("full")
                    .styles(java.util.Map.of("opacity", 0.3))
                    .required(false)
                    .build()
            ))
            .build());
        
        log.info("生成简约模板布局，共{}种", layouts.size());
        return layouts;
    }
    
    // ========== 辅助方法：创建标准元素 ==========
    
    private TemplateLayout.LayoutElement createTitleElement(String position, Integer fontSize, String color, String placeholderKey) {
        return TemplateLayout.LayoutElement.builder()
            .type("title")
            .position(position)
            .fontSize(fontSize)
            .color(color)
            .fontWeight("bold")
            .textAlign("center")
            .placeholderKey(placeholderKey)
            .required(true)
            .build();
    }
    
    private TemplateLayout.LayoutElement createSubtitleElement(String position, Integer fontSize, String color, String placeholderKey) {
        return TemplateLayout.LayoutElement.builder()
            .type("subtitle")
            .position(position)
            .fontSize(fontSize)
            .color(color)
            .textAlign("center")
            .placeholderKey(placeholderKey)
            .required(false)
            .build();
    }
    
    private TemplateLayout.LayoutElement createContentElement(String position, Integer fontSize, String color, String placeholderKey) {
        return TemplateLayout.LayoutElement.builder()
            .type("content")
            .position(position)
            .fontSize(fontSize)
            .color(color)
            .textAlign("left")
            .placeholderKey(placeholderKey)
            .required(true)
            .build();
    }
    
    private TemplateLayout.LayoutElement createListElement(String position, Integer fontSize, String color, String placeholderKey) {
        return TemplateLayout.LayoutElement.builder()
            .type("list")
            .position(position)
            .fontSize(fontSize)
            .color(color)
            .textAlign("left")
            .placeholderKey(placeholderKey)
            .required(true)
            .build();
    }
    
    private TemplateLayout.LayoutElement createNumberedListElement(String position, Integer fontSize, String color, String placeholderKey) {
        return TemplateLayout.LayoutElement.builder()
            .type("numbered-list")
            .position(position)
            .fontSize(fontSize)
            .color(color)
            .textAlign("left")
            .placeholderKey(placeholderKey)
            .required(true)
            .build();
    }
    
    private TemplateLayout.LayoutElement createImageElement(String position, String width, String placeholderKey) {
        return TemplateLayout.LayoutElement.builder()
            .type("image")
            .position(position)
            .width(width)
            .height("auto")
            .placeholderKey(placeholderKey)
            .required(false)
            .build();
    }
    
    private TemplateLayout.LayoutElement createChartElement(String position, String width, String placeholderKey) {
        return TemplateLayout.LayoutElement.builder()
            .type("chart")
            .position(position)
            .width(width)
            .height("300px")
            .placeholderKey(placeholderKey)
            .required(false)
            .build();
    }
    
    private TemplateLayout.LayoutElement createThankYouElement(String position, Integer fontSize, String color, String placeholderKey) {
        return TemplateLayout.LayoutElement.builder()
            .type("thank-you")
            .position(position)
            .fontSize(fontSize)
            .color(color)
            .textAlign("center")
            .placeholderKey(placeholderKey)
            .required(true)
            .build();
    }
    
    private TemplateLayout.LayoutElement createContactElement(String position, Integer fontSize, String color, String placeholderKey) {
        return TemplateLayout.LayoutElement.builder()
            .type("contact")
            .position(position)
            .fontSize(fontSize)
            .color(color)
            .textAlign("center")
            .placeholderKey(placeholderKey)
            .required(false)
            .build();
    }
}
