package com.pptai.dto.template;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TemplateCreateRequest {
    
    @NotBlank(message = "模板名称不能为空")
    @Size(max = 200, message = "模板名称不能超过 200 个字符")
    private String name;
    
    private String category;
    
    @Size(max = 1000, message = "描述不能超过 1000 个字符")
    private String description;
    
    private String previewUrl;
    
    private Boolean isPremium;
}
