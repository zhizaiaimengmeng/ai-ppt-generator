package com.pptai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 服务
 * 集成 DeepSeek AI 模型，用于生成 PPT 大纲和内容
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {
    
    private final RestTemplate restTemplate;
    
    @Value("${ai.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String aiApiUrl;
    
    @Value("${ai.api.key:}")
    private String aiApiKey;
    
    @Value("${ai.api.model:deepseek-chat}")
    private String aiModel;
    
    /**
     * 根据主题生成 PPT 大纲
     * @param topic 主题
     * @param slideCount 幻灯片数量
     * @return PPT 大纲 JSON
     */
    public String generateOutline(String topic, int slideCount) {
        if (aiApiKey == null || aiApiKey.isEmpty()) {
            log.warn("DeepSeek API Key 未配置，返回模拟数据");
            return generateMockOutline(topic, slideCount);
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiApiKey);
            
            String prompt = String.format(
                "为以下主题生成一个 PPT 大纲，包含%d张幻灯片：%s\n" +
                "请严格按照以下 JSON 格式返回，不要包含其他内容：\n" +
                "[{\"slideNumber\":1,\"layoutType\":\"title\",\"title\":\"主标题\",\"content\":{\"subtitle\":\"副标题\"}}]",
                slideCount, topic
            );
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiModel);
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "你是一个专业的 PPT 大纲生成助手。请根据用户输入的主题生成结构化的 PPT 大纲，返回纯 JSON 格式，不要包含 Markdown 标记。"),
                Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2000);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(aiApiUrl, request, Map.class);
            
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<?> choices = (List<?>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<?, ?> choice = (Map<?, ?>) choices.get(0);
                    Map<?, ?> message = (Map<?, ?>) choice.get("message");
                    String content = (String) message.get("content");
                    
                    // 清理 Markdown 标记
                    content = content.replace("```json", "").replace("```", "").trim();
                    
                    log.info("DeepSeek 生成大纲成功");
                    return content;
                }
            }
            
            return generateMockOutline(topic, slideCount);
            
        } catch (Exception e) {
            log.error("DeepSeek API 调用失败：{}", e.getMessage());
            return generateMockOutline(topic, slideCount);
        }
    }
    
    /**
     * 生成单张幻灯片内容
     */
    public String generateSlideContent(String topic, String slideTitle) {
        if (aiApiKey == null || aiApiKey.isEmpty()) {
            return generateMockSlideContent(slideTitle);
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiApiKey);
            
            String prompt = String.format(
                "为主题'%s'的幻灯片'%s'生成详细内容，包含 3-5 个要点。\n" +
                "请严格按照以下 JSON 格式返回：{\"points\":[\"要点 1\",\"要点 2\",\"要点 3\"]}",
                topic, slideTitle
            );
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiModel);
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "你是一个专业的 PPT 内容生成助手。请根据幻灯片标题生成要点列表，返回纯 JSON 格式。"),
                Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 500);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(aiApiUrl, request, Map.class);
            
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<?> choices = (List<?>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<?, ?> choice = (Map<?, ?>) choices.get(0);
                    Map<?, ?> message = (Map<?, ?>) choice.get("message");
                    String content = (String) message.get("content");
                    
                    // 清理 Markdown 标记
                    content = content.replace("```json", "").replace("```", "").trim();
                    
                    log.info("DeepSeek 生成幻灯片内容成功");
                    return content;
                }
            }
            
            return generateMockSlideContent(slideTitle);
            
        } catch (Exception e) {
            log.error("DeepSeek API 调用失败：{}", e.getMessage());
            return generateMockSlideContent(slideTitle);
        }
    }
    
    /**
     * 生成模拟大纲 (用于开发测试)
     */
    private String generateMockOutline(String topic, int slideCount) {
        StringBuilder sb = new StringBuilder("[");
        sb.append(String.format("{\"slideNumber\":1,\"layoutType\":\"title\",\"title\":\"%s\",\"content\":{\"subtitle\":\"AI 生成的演示文稿\"}}", topic));
        
        for (int i = 2; i <= Math.min(slideCount, 10); i++) {
            sb.append(String.format(",{\"slideNumber\":%d,\"layoutType\":\"content\",\"title\":\"第%d部分\",\"content\":{\"points\":[\"要点 1\",\"要点 2\",\"要点 3\"]}}", 
                i, i));
        }
        
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * 生成模拟幻灯片内容
     */
    private String generateMockSlideContent(String title) {
        return String.format("{\"points\":[\"关于%s的要点 1\",\"关于%s的要点 2\",\"关于%s的要点 3\"]}", 
            title, title, title);
    }
}
