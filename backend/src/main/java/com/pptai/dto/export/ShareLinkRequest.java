package com.pptai.dto.export;

import lombok.Data;

@Data
public class ShareLinkRequest {
    
    private String password;
    
    private Integer expirationDays = 7;
}
