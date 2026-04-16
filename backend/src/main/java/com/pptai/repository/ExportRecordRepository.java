package com.pptai.repository;

import com.pptai.entity.ExportRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportRecordRepository extends JpaRepository<ExportRecord, Long> {
    
    List<ExportRecord> findByUserId(Long userId);
    
    List<ExportRecord> findByProjectId(Long projectId);
    
    ExportRecord findByShareUrl(String shareUrl);
}
