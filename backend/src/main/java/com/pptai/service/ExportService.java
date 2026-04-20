package com.pptai.service;

import com.pptai.dto.export.ExportResponse;
import com.pptai.dto.export.ShareLinkRequest;
import com.pptai.entity.ExportRecord;
import com.pptai.entity.PPTProject;
import com.pptai.entity.Slide;
import com.pptai.exception.BusinessException;
import com.pptai.exception.ErrorCode;
import com.pptai.repository.ExportRecordRepository;
import com.pptai.repository.PPTProjectRepository;
import com.pptai.repository.SlideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportService {
    
    private final ExportRecordRepository exportRecordRepository;
    private final PPTProjectRepository pptProjectRepository;
    private final SlideRepository slideRepository;
    
    /**
     * 导出 PPT
     */
    @Async
    @Transactional
    public CompletableFuture<ExportResponse> exportAsync(
            Long projectId, Long userId, String format) {
        
        log.info("开始导出项目 {}，格式：{}", projectId, format);
        
        try {
            // 创建导出记录
            ExportRecord record = new ExportRecord();
            record.setUserId(userId);
            record.setProjectId(projectId);
            record.setExportFormat(format);
            record.setStatus("processing");
            record.setProgress(0);
            
            exportRecordRepository.save(record);
            log.info("创建导出记录：{}", record.getId());
            
            // 获取项目数据
            PPTProject project = pptProjectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("项目不存在"));
            
            List<Slide> slides = slideRepository.findByProjectIdOrderBySlideNumberAsc(projectId);
            
            // 根据格式导出
            String fileUrl = null;
            Long fileSize = 0L;
            
            if ("pptx".equals(format)) {
                fileUrl = exportToPPTX(project, slides);
                fileSize = getFileSize(fileUrl);
            } else if ("pdf".equals(format)) {
                fileUrl = exportToPDF(project, slides);
                fileSize = getFileSize(fileUrl);
            } else if ("image".equals(format)) {
                fileUrl = exportToImage(project, slides);
                fileSize = getFileSize(fileUrl);
            }
            
            // 更新记录
            record.setStatus("completed");
            record.setProgress(100);
            record.setFileUrl(fileUrl);
            record.setFileSize(fileSize);
            exportRecordRepository.save(record);
            
            log.info("导出完成：{}", record.getId());
            
            return CompletableFuture.completedFuture(
                ExportResponse.builder()
                    .exportId(record.getId())
                    .status("completed")
                    .downloadUrl(fileUrl)
                    .progress(100)
                    .build()
            );
            
        } catch (Exception e) {
            log.error("导出失败", e);
            
            ExportRecord record = new ExportRecord();
            record.setId(record.getId());
            record.setStatus("failed");
            record.setErrorMessage(e.getMessage());
            exportRecordRepository.save(record);
            
            return CompletableFuture.completedFuture(
                ExportResponse.builder()
                    .exportId(record.getId())
                    .status("failed")
                    .progress(0)
                    .build()
            );
        }
    }
    
    /**
     * 生成分享链接
     */
    @Transactional
    public ExportResponse createShareLink(Long projectId, Long userId, ShareLinkRequest request) {
        PPTProject project = pptProjectRepository.findById(projectId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND.getCode(),
                "项目不存在"));
        
        if (!project.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED.getCode(),
                "无权访问该项目");
        }
        
        // 创建导出记录
        ExportRecord record = new ExportRecord();
        record.setUserId(userId);
        record.setProjectId(projectId);
        record.setExportFormat("link");
        record.setStatus("completed");
        record.setProgress(100);
        record.setShareUrl(UUID.randomUUID().toString());
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            // 密码加密存储
            record.setSharePasswordHash(request.getPassword()); // TODO: 实际应该加密
        }
        
        record.setShareExpiresAt(LocalDateTime.now().plusDays(request.getExpirationDays()));
        
        exportRecordRepository.save(record);
        
        String shareUrl = "http://localhost:3000/share/" + record.getShareUrl();
        
        return ExportResponse.builder()
            .exportId(record.getId())
            .status("completed")
            .shareUrl(shareUrl)
            .expirationTime(record.getShareExpiresAt())
            .build();
    }
    
    /**
     * 获取导出状态
     */
    public ExportResponse getExportStatus(Long exportId) {
        Optional<ExportRecord> recordOpt = exportRecordRepository.findById(exportId);
        
        if (recordOpt.isEmpty()) {
            return ExportResponse.builder()
                .status("not_found")
                .build();
        }
        
        ExportRecord record = recordOpt.get();
        
        return ExportResponse.builder()
            .exportId(record.getId())
            .status(record.getStatus())
            .progress(record.getProgress())
            .downloadUrl(record.getFileUrl())
            .build();
    }
    
    /**
     * 导出为 PPTX
     */
    private String exportToPPTX(PPTProject project, List<Slide> slides) throws IOException {
        XMLSlideShow pptx = new XMLSlideShow();
        
        for (Slide slide : slides) {
            XSLFSlide pptxSlide = pptx.createSlide();
            if (slide.getTitle() != null && !slide.getTitle().isEmpty()) {
                var titleBox = pptxSlide.createTextBox();
                titleBox.setText(slide.getTitle());
            }
        }
        
        String fileName = "ppt-" + project.getId() + "-" + System.currentTimeMillis() + ".pptx";
        Path tempFile = Files.createTempFile("ppt-", ".pptx");
        
        try (FileOutputStream out = new FileOutputStream(tempFile.toFile())) {
            pptx.write(out);
        }
        
        return "/exports/" + fileName;
    }
    
    /**
     * 导出为 PDF
     */
    private String exportToPDF(PPTProject project, List<Slide> slides) throws IOException {
        // TODO: 使用 iText 或其他 PDF 库生成 PDF
        // 这里可以调用 exportToPPTX 然后转换为 PDF
        // 或者直接使用 PDF 库生成
        
        // 临时返回 PPTX 路径
        return exportToPPTX(project, slides);
    }
    
    /**
     * 导出为长图
     */
    private String exportToImage(PPTProject project, List<Slide> slides) throws IOException {
        // TODO: 使用图像处理库生成 PNG 长图
        // 可以调用 exportToPPTX 然后转换为图片
        
        // 临时返回 PPTX 路径
        return exportToPPTX(project, slides);
    }
    
    /**
     * 获取文件大小
     */
    private Long getFileSize(String filePath) {
        try {
            Path path = Path.of(filePath.replace("/exports/", "/tmp/"));
            if (Files.exists(path)) {
                return Files.size(path);
            }
        } catch (IOException e) {
            log.error("获取文件大小失败", e);
        }
        return 0L;
    }
}
