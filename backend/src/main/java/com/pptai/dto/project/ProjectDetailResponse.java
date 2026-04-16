package com.pptai.dto.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pptai.entity.Slide;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectDetailResponse {
    
    private Long id;
    
    private Long userId;
    
    private String title;
    
    private Long templateId;
    
    private String generationMode;
    
    private String status;
    
    private Integer generationProgress;
    
    private String generationMessage;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private List<Slide> slides;
}
