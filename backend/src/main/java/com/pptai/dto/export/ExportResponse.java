package com.pptai.dto.export;

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
public class ExportResponse {
    
    private Long exportId;
    
    private String status;
    
    private String downloadUrl;
    
    private String shareUrl;
    
    private LocalDateTime expirationTime;
    
    private Integer progress;
}
