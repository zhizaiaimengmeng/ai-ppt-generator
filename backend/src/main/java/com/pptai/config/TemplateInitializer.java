package com.pptai.config;

import com.pptai.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 模板初始化器
 * 应用启动时自动创建预设模板
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateInitializer implements ApplicationRunner {
    
    private final TemplateService templateService;
    
    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("开始初始化预设模板...");
            templateService.generatePresetTemplates();
            log.info("预设模板初始化完成");
        } catch (Exception e) {
            log.error("初始化预设模板失败", e);
        }
    }
}
