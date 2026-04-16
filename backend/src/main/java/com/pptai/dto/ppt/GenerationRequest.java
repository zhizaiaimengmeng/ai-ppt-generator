package com.pptai.dto.ppt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GenerationRequest {
    
    @NotBlank(message = "生成模式不能为空")
    private String mode;
    
    private String topic;
    
    @NotNull(message = "模板 ID 不能为空")
    private Long templateId;
    
    private GenerationOptions options;
    
    @Data
    public static class GenerationOptions {
        private Integer slideCount = 10;
        private Boolean includeReferences = false;
    }
}
