package com.pptai.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "ppt_projects")
public class PPTProject {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false, length = 500)
    private String title;
    
    @Column(length = 200)
    private String name;
    
    @Column(name = "template_id")
    private Long templateId;
    
    @Column(name = "generation_mode", length = 50)
    private String generationMode;
    
    @Column(nullable = false, length = 50)
    private String status = "draft";
    
    @Column(name = "generation_progress")
    private Integer generationProgress = 0;
    
    @Column(name = "generation_message", length = 500)
    private String generationMessage;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", insertable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Template template;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("slideNumber ASC")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Slide> slides = new ArrayList<>();
}
