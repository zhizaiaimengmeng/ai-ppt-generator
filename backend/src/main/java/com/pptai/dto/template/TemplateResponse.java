package com.pptai.dto.template;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateResponse {
    
    private Long id;
    
    private String name;
    
    private String category;
    
    private String description;
    
    private String previewUrl;
    
    private String templateFileUrl;
    
    private String colorScheme;
    
    private String fontScheme;
    
    private String slideLayouts;
    
    private Boolean isPremium;
    
    private Integer downloadCount;
    
    private Integer favoriteCount;
    
    private Boolean isFavorited;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
