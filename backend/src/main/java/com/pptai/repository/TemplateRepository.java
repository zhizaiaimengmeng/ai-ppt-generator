package com.pptai.repository;

import com.pptai.entity.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    
    Page<Template> findByCategory(String category, Pageable pageable);
    
    @Query("SELECT t FROM Template t WHERE t.name LIKE %:keyword% OR t.description LIKE %:keyword%")
    Page<Template> findByNameOrDescriptionContaining(@Param("keyword") String keyword, 
                                                      Pageable pageable);
    
    @Query("SELECT t FROM Template t WHERE t.category = :category AND (t.name LIKE %:keyword% OR t.description LIKE %:keyword%)")
    Page<Template> findByCategoryAndNameOrDescriptionContaining(@Param("category") String category,
                                                                 @Param("keyword") String keyword,
                                                                 Pageable pageable);
    
    List<Template> findByIsPremiumFalse();
    
    boolean existsByName(String name);
}
