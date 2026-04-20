package com.pptai.service;

import com.pptai.dto.common.PageResponse;
import com.pptai.dto.template.TemplateCreateRequest;
import com.pptai.dto.template.TemplateLayout;
import com.pptai.dto.template.TemplateResponse;
import com.pptai.entity.PPTProject;
import com.pptai.entity.Slide;
import com.pptai.entity.Template;
import com.pptai.entity.User;
import com.pptai.exception.BusinessException;
import com.pptai.exception.ErrorCode;
import com.pptai.repository.PPTProjectRepository;
import com.pptai.repository.SlideRepository;
import com.pptai.repository.TemplateRepository;
import com.pptai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模板服务
 * 负责模板的 CRUD 操作、收藏管理以及模板与 AI 内容的合成
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {
    
    private final TemplateRepository templateRepository;
    private final UserRepository userRepository;
    private final SlideRepository slideRepository;
    private final PPTProjectRepository projectRepository;
    private final TemplateDesignerService templateDesignerService;
    private final TemplatePreviewService templatePreviewService;
    
    /**
     * 分页获取模板列表
     */
    @Transactional(readOnly = true)
    public PageResponse<TemplateResponse> getTemplates(String category, String keyword, 
                                                        Pageable pageable) {
        Page<Template> templatePage;
        
        if (category != null && !category.isEmpty() && keyword != null && !keyword.isEmpty()) {
            templatePage = templateRepository.findByCategoryAndNameOrDescriptionContaining(
                category, keyword, pageable);
        } else if (category != null && !category.isEmpty()) {
            templatePage = templateRepository.findByCategory(category, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            templatePage = templateRepository.findByNameOrDescriptionContaining(keyword, pageable);
        } else {
            templatePage = templateRepository.findAll(pageable);
        }
        
        List<TemplateResponse> responses = templatePage.getContent().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        
        return PageResponse.<TemplateResponse>builder()
            .records(responses)
            .total(templatePage.getTotalElements())
            .page(pageable.getPageNumber() + 1)
            .size(pageable.getPageSize())
            .build();
    }
    
    /**
     * 获取模板详情
     */
    @Transactional(readOnly = true)
    public TemplateResponse getTemplateById(Long id) {
        Template template = templateRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.TEMPLATE_NOT_FOUND.getCode(),
                "模板不存在"));
        
        return toResponse(template);
    }
    
    /**
     * 创建模板
     */
    @Transactional
    public void createTemplate(TemplateCreateRequest request) {
        Template template = Template.builder()
            .name(request.getName())
            .category(request.getCategory())
            .description(request.getDescription())
            .previewUrl(request.getPreviewUrl())
            .isPremium(request.getIsPremium() != null ? request.getIsPremium() : false)
            .downloadCount(0)
            .favoriteCount(0)
            .build();
        
        templateRepository.save(template);
        log.info("模板创建成功：{}", template.getName());
    }
    
    /**
     * 收藏模板
     */
    @Transactional
    public void favoriteTemplate(Long templateId, Long userId) {
        Template template = templateRepository.findById(templateId)
            .orElseThrow(() -> new BusinessException(ErrorCode.TEMPLATE_NOT_FOUND.getCode(),
                "模板不存在"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(),
                "用户不存在"));
        
        if (!user.getFavoriteTemplates().contains(template)) {
            user.getFavoriteTemplates().add(template);
            template.setFavoriteCount(template.getFavoriteCount() + 1);
            userRepository.save(user);
            templateRepository.save(template);
            log.info("用户 {} 收藏了模板 {}", userId, template.getName());
        }
    }
    
    /**
     * 取消收藏模板
     */
    @Transactional
    public void unfavoriteTemplate(Long templateId, Long userId) {
        Template template = templateRepository.findById(templateId)
            .orElseThrow(() -> new BusinessException(ErrorCode.TEMPLATE_NOT_FOUND.getCode(),
                "模板不存在"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND.getCode(),
                "用户不存在"));
        
        if (user.getFavoriteTemplates().contains(template)) {
            user.getFavoriteTemplates().remove(template);
            template.setFavoriteCount(Math.max(0, template.getFavoriteCount() - 1));
            userRepository.save(user);
            templateRepository.save(template);
            log.info("用户 {} 取消了收藏模板 {}", userId, template.getName());
        }
    }
    
    /**
     * 获取免费模板列表
     */
    @Transactional(readOnly = true)
    public List<Template> getFreeTemplates() {
        return templateRepository.findByIsPremiumFalse();
    }
    
    /**
     * 根据模板创建 PPT（模板+AI 内容填充）
     * 这是核心合成方法：将 AI 生成的内容填充到模板布局中
     */
    @Transactional
    public List<Slide> applyTemplate(PPTProject project, List<Slide> aiGeneratedSlides) {
        // 获取模板
        Template template = templateRepository.findById(project.getTemplateId())
            .orElseThrow(() -> new BusinessException(ErrorCode.TEMPLATE_NOT_FOUND.getCode(),
                "模板不存在"));
        
        log.info("开始应用模板 {} 到项目 {}，共{}张幻灯片", 
            template.getName(), project.getName(), aiGeneratedSlides.size());
        
        // 获取模板布局定义
        List<TemplateLayout> layouts = getTemplateLayouts(template);
        
        // 将 AI 生成的内容填充到模板布局中
        List<Slide> styledSlides = new ArrayList<>();
        for (int i = 0; i < aiGeneratedSlides.size(); i++) {
            Slide slide = aiGeneratedSlides.get(i);
            
            // 为每张幻灯片选择合适的布局（循环使用模板布局）
            TemplateLayout layout = layouts.get(i % layouts.size());
            
            // 应用模板样式
            Slide styledSlide = applyTemplateToSlide(slide, template, layout);
            styledSlides.add(styledSlide);
        }
        
        log.info("模板应用完成，生成{}张带样式的幻灯片", styledSlides.size());
        
        return styledSlides;
    }
    
    /**
     * 将模板样式应用到单个幻灯片
     */
    private Slide applyTemplateToSlide(Slide slide, Template template, TemplateLayout layout) {
        // 应用布局类型
        slide.setLayoutType(layout.getLayoutType());
        
        // 应用背景样式（背景色或渐变）
        if (layout.getGradient() != null) {
            slide.setBackgroundGradientObject(layout.getGradient());
        } else if (layout.getBackgroundColor() != null) {
            slide.setBackgroundColor(layout.getBackgroundColor());
        } else if (layout.getBackgroundImage() != null) {
            slide.setBackgroundImage(layout.getBackgroundImage());
        }
        
        // 应用元素样式
        Map<String, Object> styledContent = new HashMap<>();
        try {
            if (slide.getContent() != null) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                Object contentObj = mapper.readValue(slide.getContent(), Object.class);
                if (contentObj instanceof Map) {
                    styledContent.putAll((Map<String, Object>) contentObj);
                }
            }
        } catch (Exception e) {
            log.warn("解析幻灯片内容失败", e);
        }
        
        // 根据布局元素配置，设置内容样式
        if (layout.getElements() != null) {
            for (TemplateLayout.LayoutElement element : layout.getElements()) {
                String placeholderKey = element.getPlaceholderKey();
                if (placeholderKey != null && styledContent.containsKey(placeholderKey)) {
                    Object content = styledContent.get(placeholderKey);
                    styledContent.put(placeholderKey, wrapWithStyle(content, element));
                }
            }
        }
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            slide.setContent(mapper.writeValueAsString(styledContent));
        } catch (Exception e) {
            log.error("序列化幻灯片内容失败", e);
        }
        
        log.info("应用模板样式到幻灯片：{} - {}", slide.getSlideNumber(), layout.getLayoutType());
        
        return slide;
    }
    
    /**
     * 为内容添加样式信息
     */
    private Object wrapWithStyle(Object content, TemplateLayout.LayoutElement element) {
        // 如果内容是字符串，直接返回
        // 实际应用中，这里可以构建包含样式信息的对象
        // 例如：{"text": "内容", "style": {"fontSize": 16, "color": "#333333"}}
        return content;
    }
    
    /**
     * 获取模板布局定义
     * 如果模板已有布局定义，直接使用；否则根据模板类型生成默认布局
     */
    private List<TemplateLayout> getTemplateLayouts(Template template) {
        // 优先从模板中读取已保存的布局定义
        if (template.getSlideLayouts() != null && !template.getSlideLayouts().isEmpty()) {
            log.info("使用模板已保存的布局定义");
            // 这里需要从 JSON 解析为 TemplateLayout 对象
            // 当前简化处理，根据模板类型生成默认布局
        }
        
        // 根据模板类型生成对应的布局
        String category = template.getCategory() != null ? template.getCategory().toLowerCase() : "business";
        
        log.info("根据模板类型 {} 生成默认布局", category);
        
        switch (category) {
            case "tech":
            case "technology":
                return templateDesignerService.generateTechTemplate();
            case "education":
                return templateDesignerService.generateEducationTemplate();
            case "minimal":
            case "simple":
                return templateDesignerService.generateMinimalTemplate();
            case "business":
            default:
                return templateDesignerService.generateBusinessTemplate();
        }
    }
    
    /**
     * 将实体转换为 DTO
     */
    private TemplateResponse toResponse(Template template) {
        return TemplateResponse.builder()
            .id(template.getId())
            .name(template.getName())
            .category(template.getCategory())
            .description(template.getDescription())
            .previewUrl(template.getPreviewUrl())
            .templateFileUrl(template.getTemplateFileUrl())
            .colorScheme(template.getColorScheme())
            .fontScheme(template.getFontScheme())
            .slideLayouts(template.getSlideLayouts())
            .isPremium(template.getIsPremium())
            .downloadCount(template.getDownloadCount())
            .favoriteCount(template.getFavoriteCount())
            .createdAt(template.getCreatedAt())
            .updatedAt(template.getUpdatedAt())
            .build();
    }
    
    /**
     * 批量生成预定义模板
     * 在项目初始化或需要时调用此方法生成所有风格的模板
     */
    @Transactional
    public void generatePresetTemplates() {
        log.info("开始生成预定义模板...");
        
        // 商务风格模板
        createPresetTemplate(
            "商务风",
            "business",
            "专业、稳重的商务风格模板，适用于商务汇报、项目路演等正式场合",
            templateDesignerService.generateBusinessTemplate(),
            false
        );
        
        // 科技风格模板
        createPresetTemplate(
            "科技感",
            "tech",
            "现代、炫酷的科技风格模板，适用于产品发布、技术分享等场合",
            templateDesignerService.generateTechTemplate(),
            false
        );
        
        // 教育风格模板
        createPresetTemplate(
            "教育风",
            "education",
            "清新、活泼的教育风格模板，适用于教学课件、培训讲座等场合",
            templateDesignerService.generateEducationTemplate(),
            false
        );
        
        // 简约风格模板
        createPresetTemplate(
            "简约风",
            "minimal",
            "简洁、大气的简约风格模板，适用于各种正式和非正式场合",
            templateDesignerService.generateMinimalTemplate(),
            false
        );
        
        log.info("预定义模板生成完成");
    }
    
    /**
     * 创建单个预定义模板
     */
    private void createPresetTemplate(String name, String category, String description, 
                                       List<TemplateLayout> layouts, Boolean isPremium) {
        // 检查模板是否已存在
        if (templateRepository.existsByName(name)) {
            log.info("模板 {} 已存在，跳过", name);
            return;
        }
        
        try {
            // 生成预览图
            String previewImageBase64 = templatePreviewService.generatePreview(layouts);
            
            // 创建模板
            Template template = Template.builder()
                .name(name)
                .category(category)
                .description(description)
                .previewUrl("data:image/png;base64," + previewImageBase64)
                .isPremium(isPremium)
                .downloadCount(0)
                .favoriteCount(0)
                .build();
            
            templateRepository.save(template);
            log.info("创建预定义模板：{} - {}", name, category);
            
        } catch (Exception e) {
            log.error("创建预定义模板 {} 失败", name, e);
        }
    }
}
