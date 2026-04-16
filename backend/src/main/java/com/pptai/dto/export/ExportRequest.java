package com.pptai.dto.export;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExportRequest {
    
    @NotBlank(message = "导出格式不能为空")
    private String format;
}
