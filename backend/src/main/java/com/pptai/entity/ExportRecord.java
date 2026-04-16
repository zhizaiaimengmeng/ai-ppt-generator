package com.pptai.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "export_records")
public class ExportRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "project_id", nullable = false)
    private Long projectId;
    
    @Column(name = "export_format", nullable = false, length = 50)
    private String exportFormat;
    
    @Column(name = "file_url", length = 500)
    private String fileUrl;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(nullable = false, length = 50)
    private String status = "pending";
    
    @Column(nullable = false)
    private Integer progress = 0;
    
    @Column(name = "error_message", length = 500)
    private String errorMessage;
    
    @Column(name = "share_url", length = 500)
    private String shareUrl;
    
    @Column(name = "share_password_hash", length = 255)
    private String sharePasswordHash;
    
    @Column(name = "share_expires_at")
    private LocalDateTime shareExpiresAt;
    
    @Column(name = "download_count")
    private Integer downloadCount = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
