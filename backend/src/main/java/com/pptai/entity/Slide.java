package com.pptai.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "slides")
public class Slide {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "project_id", nullable = false)
    private Long projectId;
    
    @Column(name = "slide_number", nullable = false)
    private Integer slideNumber;
    
    @Column(name = "layout_type", length = 50)
    private String layoutType;
    
    @Column(length = 500)
    private String title;
    
    @Column(columnDefinition = "JSON")
    private String content;
    
    @Column(name = "background_color", length = 50)
    private String backgroundColor;
    
    @Column(name = "background_gradient", columnDefinition = "JSON")
    private String backgroundGradient;
    
    @Column(name = "background_image", length = 500)
    private String backgroundImage;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private PPTProject project;
    
    /**
     * 获取背景渐变对象
     */
    public com.pptai.dto.template.TemplateLayout.GradientConfig getBackgroundGradientObject() {
        if (backgroundGradient == null || backgroundGradient.isEmpty()) {
            return null;
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(backgroundGradient, com.pptai.dto.template.TemplateLayout.GradientConfig.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 设置背景渐变对象
     */
    public void setBackgroundGradientObject(com.pptai.dto.template.TemplateLayout.GradientConfig gradient) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            this.backgroundGradient = mapper.writeValueAsString(gradient);
        } catch (Exception e) {
            this.backgroundGradient = null;
        }
    }
}
