package com.pptai.controller;

import com.pptai.dto.ApiResponse;
import com.pptai.dto.project.ProjectDetailResponse;
import com.pptai.dto.project.ProjectListResponse;
import com.pptai.dto.project.ProjectRequest;
import com.pptai.dto.user.UserResponse;
import com.pptai.entity.Slide;
import com.pptai.service.ProjectService;
import com.pptai.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    
    private final ProjectService projectService;
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<ProjectListResponse>> getProjects(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        ProjectListResponse projects = projectService.getProjects(user.getId(), page, size, keyword);
        
        return ResponseEntity.ok(ApiResponse.success(projects));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDetailResponse>> getProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        ProjectDetailResponse project = projectService.getProjectDetail(id, user.getId());
        
        return ResponseEntity.ok(ApiResponse.success(project));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectDetailResponse>> createProject(
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        var project = projectService.createProject(user.getId(), request);
        
        var detail = ProjectDetailResponse.builder()
            .id(project.getId())
            .title(project.getTitle())
            .build();
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("项目创建成功", detail));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        projectService.updateProject(id, user.getId(), request);
        
        return ResponseEntity.ok(ApiResponse.success("项目更新成功", null));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        projectService.deleteProject(id, user.getId());
        
        return ResponseEntity.ok(ApiResponse.success("项目删除成功", null));
    }
    
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<ApiResponse<ProjectDetailResponse>> duplicateProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        var newProject = projectService.duplicateProject(id, user.getId());
        
        var detail = ProjectDetailResponse.builder()
            .id(newProject.getId())
            .title(newProject.getTitle())
            .build();
        
        return ResponseEntity.ok(ApiResponse.success("项目复制成功", detail));
    }
    
    @PutMapping("/{id}/slides")
    public ResponseEntity<ApiResponse<Void>> updateSlides(
            @PathVariable Long id,
            @RequestBody List<Slide> slides,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UserResponse user = userService.getCurrentUser(userDetails.getUsername());
        projectService.updateSlides(id, user.getId(), slides);
        
        return ResponseEntity.ok(ApiResponse.success("幻灯片更新成功", null));
    }
}
