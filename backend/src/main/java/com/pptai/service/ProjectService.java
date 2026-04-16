package com.pptai.service;

import com.pptai.dto.project.ProjectDetailResponse;
import com.pptai.dto.project.ProjectListResponse;
import com.pptai.dto.project.ProjectRequest;
import com.pptai.entity.PPTProject;
import com.pptai.entity.Slide;
import com.pptai.repository.PPTProjectRepository;
import com.pptai.repository.SlideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final PPTProjectRepository pptProjectRepository;
    private final SlideRepository slideRepository;
    
    @Transactional(readOnly = true)
    public ProjectListResponse getProjects(Long userId, Integer page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PPTProject> projectPage;
        
        if (keyword != null && !keyword.isEmpty()) {
            projectPage = pptProjectRepository.findByUserIdAndTitleContaining(userId, keyword, pageable);
        } else {
            projectPage = pptProjectRepository.findByUserId(userId, pageable);
        }
        
        List<ProjectListResponse.ProjectInfo> projects = projectPage.getContent().stream()
            .map(project -> ProjectListResponse.ProjectInfo.builder()
                .id(project.getId())
                .userId(project.getUserId())
                .title(project.getTitle())
                .status(project.getStatus())
                .generationProgress(project.getGenerationProgress())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build())
            .collect(Collectors.toList());
        
        return ProjectListResponse.builder()
            .records(projects)
            .total(projectPage.getTotalElements())
            .page(page)
            .size(size)
            .build();
    }
    
    @Transactional(readOnly = true)
    public ProjectDetailResponse getProjectDetail(Long projectId, Long userId) {
        PPTProject project = pptProjectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("项目不存在"));
        
        if (!project.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该项目");
        }
        
        List<Slide> slides = slideRepository.findByProjectIdOrderBySlideNumberAsc(projectId);
        
        return ProjectDetailResponse.builder()
            .id(project.getId())
            .userId(project.getUserId())
            .title(project.getTitle())
            .templateId(project.getTemplateId())
            .generationMode(project.getGenerationMode())
            .status(project.getStatus())
            .generationProgress(project.getGenerationProgress())
            .generationMessage(project.getGenerationMessage())
            .createdAt(project.getCreatedAt())
            .updatedAt(project.getUpdatedAt())
            .slides(slides)
            .build();
    }
    
    @Transactional
    public PPTProject createProject(Long userId, ProjectRequest request) {
        PPTProject project = new PPTProject();
        project.setUserId(userId);
        project.setTitle(request.getTitle());
        project.setStatus("draft");
        
        return pptProjectRepository.save(project);
    }
    
    @Transactional
    public void updateProject(Long projectId, Long userId, ProjectRequest request) {
        PPTProject project = pptProjectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("项目不存在"));
        
        if (!project.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改该项目");
        }
        
        if (request.getTitle() != null) {
            project.setTitle(request.getTitle());
        }
        
        pptProjectRepository.save(project);
    }
    
    @Transactional
    public void deleteProject(Long projectId, Long userId) {
        PPTProject project = pptProjectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("项目不存在"));
        
        if (!project.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该项目");
        }
        
        pptProjectRepository.deleteById(projectId);
    }
    
    @Transactional
    public PPTProject duplicateProject(Long projectId, Long userId) {
        PPTProject sourceProject = pptProjectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("项目不存在"));
        
        if (!sourceProject.getUserId().equals(userId)) {
            throw new RuntimeException("无权复制该项目");
        }
        
        // 创建新项目
        PPTProject newProject = new PPTProject();
        newProject.setUserId(userId);
        newProject.setTitle(sourceProject.getTitle() + " - 副本");
        newProject.setTemplateId(sourceProject.getTemplateId());
        newProject.setGenerationMode(sourceProject.getGenerationMode());
        newProject.setStatus("draft");
        newProject.setGenerationProgress(100);
        
        pptProjectRepository.save(newProject);
        log.info("复制项目：{} -> {}", projectId, newProject.getId());
        
        // 复制幻灯片
        List<Slide> sourceSlides = slideRepository.findByProjectIdOrderBySlideNumberAsc(projectId);
        for (Slide slide : sourceSlides) {
            Slide newSlide = new Slide();
            newSlide.setProjectId(newProject.getId());
            newSlide.setSlideNumber(slide.getSlideNumber());
            newSlide.setLayoutType(slide.getLayoutType());
            newSlide.setTitle(slide.getTitle());
            newSlide.setContent(slide.getContent());
            newSlide.setBackgroundColor(slide.getBackgroundColor());
            slideRepository.save(newSlide);
        }
        
        return newProject;
    }
    
    @Transactional
    public void updateSlides(Long projectId, Long userId, List<Slide> slides) {
        PPTProject project = pptProjectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("项目不存在"));
        
        if (!project.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改该项目");
        }
        
        // 删除旧幻灯片
        slideRepository.deleteByProjectId(projectId);
        
        // 保存新幻灯片
        for (int i = 0; i < slides.size(); i++) {
            Slide slide = slides.get(i);
            slide.setProjectId(projectId);
            slide.setSlideNumber(i + 1);
            slideRepository.save(slide);
        }
        
        log.info("更新项目 {} 的幻灯片，共{}张", projectId, slides.size());
    }
}
