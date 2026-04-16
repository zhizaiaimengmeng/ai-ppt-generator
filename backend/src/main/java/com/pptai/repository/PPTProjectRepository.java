package com.pptai.repository;

import com.pptai.entity.PPTProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PPTProjectRepository extends JpaRepository<PPTProject, Long> {
    
    Page<PPTProject> findByUserId(Long userId, Pageable pageable);
    
    List<PPTProject> findByUserIdOrderByUpdatedAtDesc(Long userId);
    
    @Query("SELECT p FROM PPTProject p WHERE p.userId = :userId AND p.title LIKE %:keyword% ORDER BY p.updatedAt DESC")
    Page<PPTProject> findByUserIdAndTitleContaining(@Param("userId") Long userId, 
                                                     @Param("keyword") String keyword, 
                                                     Pageable pageable);
    
    long countByUserId(Long userId);
}
