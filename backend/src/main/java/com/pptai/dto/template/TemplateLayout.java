package com.pptai.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 模板布局定义
 * 用于描述单张幻灯片的布局结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateLayout {
    
    /**
     * 布局类型
     * - title: 封面页
     * - content: 内容页
     * - image-content: 图文页
     * - list: 列表页
     * - end: 结束页
     * - section: 章节页
     */
    private String layoutType;
    
    /**
     * 布局名称
     */
    private String name;
    
    /**
     * 背景颜色（十六进制）
     */
    private String backgroundColor;
    
    /**
     * 背景渐变配置
     */
    private GradientConfig gradient;
    
    /**
     * 背景图片 URL
     */
    private String backgroundImage;
    
    /**
     * 布局元素列表
     */
    private List<LayoutElement> elements;
    
    /**
     * 预设内容占位符
     */
    private Map<String, Object> placeholders;
    
    /**
     * 渐变配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GradientConfig {
        
        /**
         * 渐变类型：linear, radial
         */
        private String type;
        
        /**
         * 渐变方向：to right, to bottom, 45deg 等
         */
        private String direction;
        
        /**
         * 渐变颜色数组
         */
        private List<String> colors;
        
        /**
         * 颜色停止位置（百分比）
         */
        private List<String> stops;
    }
    
    /**
     * 布局元素
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LayoutElement {
        
        /**
         * 元素类型
         * - title: 标题
         * - subtitle: 副标题
         * - content: 正文内容
         * - list: 列表
         * - numbered-list: 编号列表
         * - image: 图片
         * - chart: 图表
         * - icon: 图标
         * - quote: 引用
         * - thank-you: 致谢语
         * - contact: 联系信息
         * - logo: Logo
         * - footer: 页脚
         */
        private String type;
        
        /**
         * 元素位置
         * - top: 顶部
         * - center: 居中
         * - bottom: 底部
         * - left: 左侧
         * - right: 右侧
         * - top-left: 左上
         * - top-right: 右上
         * - bottom-left: 左下
         * - bottom-right: 右下
         */
        private String position;
        
        /**
         * 字体大小（磅）
         */
        private Integer fontSize;
        
        /**
         * 字体颜色（十六进制）
         */
        private String color;
        
        /**
         * 字体粗细：normal, bold, bolder
         */
        private String fontWeight;
        
        /**
         * 宽度（像素或百分比）
         */
        private String width;
        
        /**
         * 高度（像素或百分比）
         */
        private String height;
        
        /**
         * 对齐方式：left, center, right, justify
         */
        private String textAlign;
        
        /**
         * 元素样式类名
         */
        private String className;
        
        /**
         * 额外样式配置
         */
        private Map<String, Object> styles;
        
        /**
         * 内容占位符键（用于 AI 内容填充）
         */
        private String placeholderKey;
        
        /**
         * 是否必需元素（AI 生成时必须提供）
         */
        private Boolean required;
    }
}
