package com.pptai.dto.project;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ProjectListResponse {
    
    private List<ProjectInfo> records;
    
    private Long total;
    
    private Integer page;
    
    private Integer size;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectInfo {
        private Long id;
        private Long userId;
        private String title;
        private String status;
        private Integer generationProgress;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
