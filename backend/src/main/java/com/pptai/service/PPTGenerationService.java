package com.pptai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pptai.dto.ppt.GenerationRequest;
import com.pptai.dto.ppt.GenerationResponse;
import com.pptai.entity.PPTProject;
import com.pptai.entity.Slide;
import com.pptai.repository.PPTProjectRepository;
import com.pptai.repository.SlideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PPTGenerationService {
    
    private final AIService aiService;
    private final PPTProjectRepository pptProjectRepository;
    private final SlideRepository slideRepository;
    private final ObjectMapper objectMapper;
    
    /**
     * 异步生成 PPT
     */
    @Async
    @Transactional
    public CompletableFuture<GenerationResponse> generateAsync(
            GenerationRequest request, Long userId) {
        
        log.info("开始异步生成 PPT，主题：{}", request.getTopic());
        
        try {
            // 创建项目
            PPTProject project = new PPTProject();
            project.setUserId(userId);
            project.setTitle(request.getTopic());
            project.setTemplateId(request.getTemplateId());
            project.setGenerationMode(request.getMode());
            project.setStatus("processing");
            project.setGenerationProgress(0);
            project.setGenerationMessage("正在生成大纲...");
            
            pptProjectRepository.save(project);
            log.info("创建项目：{}", project.getId());
            
            // 生成大纲
            updateProgress(project, 20, "正在生成大纲...");
            String outlineJson = aiService.generateOutline(
                request.getTopic(),
                request.getOptions() != null ? 
                    request.getOptions().getSlideCount() : 10
            );
            
            // 解析大纲并创建幻灯片
            updateProgress(project, 50, "正在创建幻灯片...");
            List<Slide> slides = parseAndCreateSlides(project, outlineJson);
            slideRepository.saveAll(slides);
            
            // 生成详细内容
            updateProgress(project, 80, "正在生成详细内容...");
            generateSlideDetails(project, slides, request.getTopic());
            
            // 完成
            updateProgress(project, 100, "生成完成");
            project.setStatus("completed");
            pptProjectRepository.save(project);
            
            log.info("PPT 生成完成，项目 ID: {}, 幻灯片数量：{}", project.getId(), slides.size());
            
            return CompletableFuture.completedFuture(
                GenerationResponse.builder()
                    .projectId(project.getId())
                    .status("completed")
                    .estimatedTime(60)
                    .build()
            );
            
        } catch (Exception e) {
            log.error("PPT 生成失败", e);
            PPTProject project = new PPTProject();
            project.setId(project.getId());
            project.setStatus("failed");
            project.setGenerationMessage("生成失败：" + e.getMessage());
            pptProjectRepository.save(project);
            
            return CompletableFuture.completedFuture(
                GenerationResponse.builder()
                    .projectId(project.getId())
                    .status("failed")
                    .estimatedTime(0)
                    .build()
            );
        }
    }
    
    /**
     * 获取生成进度
     */
    public GenerationResponse getGenerationStatus(Long projectId) {
        Optional<PPTProject> projectOpt = pptProjectRepository.findById(projectId);
        
        if (projectOpt.isEmpty()) {
            return GenerationResponse.builder()
                .status("not_found")
                .build();
        }
        
        PPTProject project = projectOpt.get();
        
        return GenerationResponse.builder()
            .projectId(project.getId())
            .status(project.getStatus())
            .estimatedTime(60 - (project.getGenerationProgress() / 100 * 60))
            .build();
    }
    
    /**
     * 解析大纲并创建幻灯片
     */
    private List<Slide> parseAndCreateSlides(PPTProject project, String outlineJson) {
        try {
            JsonNode slidesNode = objectMapper.readTree(outlineJson);
            
            for (int i = 0; i < slidesNode.size(); i++) {
                JsonNode slideNode = slidesNode.get(i);
                
                Slide slide = new Slide();
                slide.setProjectId(project.getId());
                slide.setSlideNumber(slideNode.path("slideNumber").asInt(i + 1));
                slide.setLayoutType(slideNode.path("layoutType").asText("content"));
                slide.setTitle(slideNode.path("title").asText("幻灯片 " + (i + 1)));
                
                JsonNode contentNode = slideNode.path("content");
                slide.setContent(objectMapper.writeValueAsString(contentNode));
                
                slideRepository.save(slide);
            }
            
            return slideRepository.findByProjectIdOrderBySlideNumberAsc(project.getId());
            
        } catch (Exception e) {
            log.error("解析大纲失败", e);
            throw new RuntimeException("解析大纲失败", e);
        }
    }
    
    /**
     * 生成幻灯片详细内容
     */
    @Async
    public void generateSlideDetails(PPTProject project, List<Slide> slides, String topic) {
        for (Slide slide : slides) {
            try {
                String contentJson = aiService.generateSlideContent(topic, slide.getTitle());
                slide.setContent(contentJson);
                slideRepository.save(slide);
            } catch (Exception e) {
                log.error("生成幻灯片内容失败：{}", slide.getTitle(), e);
            }
        }
    }
    
    /**
     * 更新生成进度
     */
    private void updateProgress(PPTProject project, int progress, String message) {
        project.setGenerationProgress(progress);
        project.setGenerationMessage(message);
        pptProjectRepository.save(project);
        log.info("项目 {} 进度：{}% - {}", project.getId(), progress, message);
    }
}
