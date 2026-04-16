package com.pptai.service;

import com.pptai.dto.template.TemplateLayout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * 模板预览服务
 * 用于生成模板布局的预览图
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TemplatePreviewService {
    
    private static final int PREVIEW_WIDTH = 320;
    private static final int PREVIEW_HEIGHT = 180; // 16:9 比例
    
    /**
     * 为模板布局生成预览图（Base64 PNG）
     */
    public String generatePreview(List<TemplateLayout> layouts) {
        if (layouts == null || layouts.isEmpty()) {
            return generateDefaultPreview();
        }
        
        // 取第一个布局作为预览
        TemplateLayout firstLayout = layouts.get(0);
        
        try {
            BufferedImage image = createPreviewImage(firstLayout);
            return convertToBase64(image);
        } catch (Exception e) {
            log.error("生成预览图失败", e);
            return generateDefaultPreview();
        }
    }
    
    /**
     * 为幻灯片布局生成预览图
     */
    public String generateSlidePreview(TemplateLayout layout) {
        try {
            BufferedImage image = createPreviewImage(layout);
            return convertToBase64(image);
        } catch (Exception e) {
            log.error("生成幻灯片预览图失败", e);
            return generateDefaultPreview();
        }
    }
    
    /**
     * 创建预览图片
     */
    private BufferedImage createPreviewImage(TemplateLayout layout) {
        BufferedImage image = new BufferedImage(PREVIEW_WIDTH, PREVIEW_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // 渲染抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 填充背景
        if (layout.getGradient() != null) {
            // 绘制渐变背景
            fillGradientBackground(g2d, layout.getGradient());
        } else if (layout.getBackgroundColor() != null) {
            // 填充纯色背景
            Color bgColor = Color.decode(layout.getBackgroundColor());
            g2d.setColor(bgColor);
            g2d.fillRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
        } else {
            // 默认白色背景
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
        }
        
        // 绘制元素占位符
        if (layout.getElements() != null) {
            for (TemplateLayout.LayoutElement element : layout.getElements()) {
                drawElementPlaceholder(g2d, element);
            }
        }
        
        // 绘制布局类型标签
        drawLayoutTypeLabel(g2d, layout.getLayoutType());
        
        g2d.dispose();
        return image;
    }
    
    /**
     * 填充渐变背景
     */
    private void fillGradientBackground(Graphics2D g2d, TemplateLayout.GradientConfig gradient) {
        List<String> colors = gradient.getColors();
        if (colors == null || colors.isEmpty()) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
            return;
        }
        
        Color startColor = Color.decode(colors.get(0));
        Color endColor = colors.size() > 1 ? Color.decode(colors.get(1)) : startColor;
        
        // 创建渐变
        GradientPaint gradientPaint = new GradientPaint(
            0, 0, startColor,
            PREVIEW_WIDTH, PREVIEW_HEIGHT, endColor
        );
        g2d.setPaint(gradientPaint);
        g2d.fillRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
    }
    
    /**
     * 绘制元素占位符
     */
    private void drawElementPlaceholder(Graphics2D g2d, TemplateLayout.LayoutElement element) {
        String type = element.getType();
        String position = element.getPosition();
        
        // 计算元素位置
        Rectangle bounds = calculateElementBounds(position, element.getWidth(), element.getHeight(), type);
        
        // 根据元素类型绘制不同的占位符
        switch (type) {
            case "title":
            case "subtitle":
                drawTitlePlaceholder(g2d, bounds, type.equals("title"));
                break;
            case "content":
            case "list":
            case "numbered-list":
                drawContentPlaceholder(g2d, bounds);
                break;
            case "image":
            case "chart":
                drawImagePlaceholder(g2d, bounds);
                break;
            case "thank-you":
                drawThankYouPlaceholder(g2d, bounds);
                break;
            default:
                drawGenericPlaceholder(g2d, bounds);
        }
    }
    
    /**
     * 计算元素边界
     */
    private Rectangle calculateElementBounds(String position, String width, String height, String type) {
        int padding = 20;
        int x = padding;
        int y = padding;
        int w = PREVIEW_WIDTH - 2 * padding;
        int h = PREVIEW_HEIGHT - 2 * padding;
        
        if ("title".equals(type)) {
            h = 40;
            y = PREVIEW_HEIGHT / 2 - h / 2;
        } else if ("thank-you".equals(type)) {
            h = 40;
            y = PREVIEW_HEIGHT / 2 - h / 2;
        } else if ("content".equals(type) || "list".equals(type)) {
            h = 80;
            y = PREVIEW_HEIGHT / 2 - h / 2 + 30;
        } else if ("image".equals(type) || "chart".equals(type)) {
            if ("left".equals(position)) {
                w = (PREVIEW_WIDTH - 3 * padding) / 2;
            } else if ("right".equals(position)) {
                x = PREVIEW_WIDTH / 2 + padding / 2;
                w = (PREVIEW_WIDTH - 3 * padding) / 2;
            }
            h = 80;
            y = PREVIEW_HEIGHT / 2 - h / 2 + 20;
        }
        
        return new Rectangle(x, y, w, h);
    }
    
    /**
     * 绘制标题占位符
     */
    private void drawTitlePlaceholder(Graphics2D g2d, Rectangle bounds, boolean isTitle) {
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        // 绘制标题线条
        g2d.setColor(new Color(255, 255, 255, 150));
        int lineHeight = 8;
        int lineY = bounds.y + bounds.height / 2 - lineHeight / 2;
        g2d.fillRect(bounds.x + 20, lineY, bounds.width - 40, lineHeight);
    }
    
    /**
     * 绘制内容占位符
     */
    private void drawContentPlaceholder(Graphics2D g2d, Rectangle bounds) {
        g2d.setColor(new Color(200, 200, 200, 100));
        g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        // 绘制文本行
        g2d.setColor(new Color(150, 150, 150, 150));
        int lineHeight = 6;
        int lineSpacing = 12;
        for (int i = 0; i < 4; i++) {
            int y = bounds.y + 15 + i * lineSpacing;
            g2d.fillRect(bounds.x + 10, y, bounds.width - 20, lineHeight);
        }
    }
    
    /**
     * 绘制图片占位符
     */
    private void drawImagePlaceholder(Graphics2D g2d, Rectangle bounds) {
        g2d.setColor(new Color(180, 180, 180, 80));
        g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        g2d.setColor(new Color(150, 150, 150, 150));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        // 绘制图片图标（十字交叉线）
        g2d.setColor(new Color(150, 150, 150, 100));
        g2d.drawLine(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height);
        g2d.drawLine(bounds.x + bounds.width, bounds.y, bounds.x, bounds.y + bounds.height);
    }
    
    /**
     * 绘制致谢占位符
     */
    private void drawThankYouPlaceholder(Graphics2D g2d, Rectangle bounds) {
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "Thank You";
        int textWidth = fm.stringWidth(text);
        g2d.drawString(text, bounds.x + (bounds.width - textWidth) / 2, bounds.y + bounds.height / 2 + 8);
    }
    
    /**
     * 绘制通用占位符
     */
    private void drawGenericPlaceholder(Graphics2D g2d, Rectangle bounds) {
        g2d.setColor(new Color(200, 200, 200, 60));
        g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        g2d.setColor(new Color(150, 150, 150, 100));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
    
    /**
     * 绘制布局类型标签
     */
    private void drawLayoutTypeLabel(Graphics2D g2d, String layoutType) {
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRect(0, PREVIEW_HEIGHT - 25, PREVIEW_WIDTH, 25);
        
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String label = layoutType != null ? layoutType.toUpperCase() : "LAYOUT";
        int textWidth = fm.stringWidth(label);
        g2d.drawString(label, (PREVIEW_WIDTH - textWidth) / 2, PREVIEW_HEIGHT - 8);
    }
    
    /**
     * 将图片转换为 Base64
     */
    private String convertToBase64(BufferedImage image) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] bytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    /**
     * 生成默认预览图
     */
    private String generateDefaultPreview() {
        BufferedImage image = new BufferedImage(PREVIEW_WIDTH, PREVIEW_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // 灰色背景
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
        
        // 绘制问号
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "?";
        int textWidth = fm.stringWidth(text);
        g2d.drawString(text, (PREVIEW_WIDTH - textWidth) / 2, PREVIEW_HEIGHT / 2);
        
        g2d.dispose();
        
        try {
            return convertToBase64(image);
        } catch (Exception e) {
            return "";
        }
    }
}
