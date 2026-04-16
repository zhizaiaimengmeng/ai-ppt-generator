package com.pptai.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "templates")
public class Template {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(length = 100)
    private String category;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "preview_url", length = 500)
    private String previewUrl;
    
    @Column(name = "template_file_url", length = 500)
    private String templateFileUrl;
    
    @Column(name = "color_scheme", columnDefinition = "JSON")
    private String colorScheme;
    
    @Column(name = "font_scheme", columnDefinition = "JSON")
    private String fontScheme;
    
    @Column(name = "slide_layouts", columnDefinition = "JSON")
    private String slideLayouts;
    
    @Column(name = "is_premium")
    private Boolean isPremium = false;
    
    @Column(name = "download_count")
    private Integer downloadCount = 0;
    
    @Column(name = "favorite_count")
    private Integer favoriteCount = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToMany(mappedBy = "favoriteTemplates")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<User> favoritedBy = new ArrayList<>();
}
