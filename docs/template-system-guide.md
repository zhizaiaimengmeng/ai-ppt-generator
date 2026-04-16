# 模板系统使用说明

## 概述

模板系统是 AI 生成 PPT 应用的核心组件，负责定义幻灯片的视觉设计、布局结构和样式规范。模板系统与 AI 内容生成分离，确保：

1. **降低 AI 调用成本** - AI 只负责生成结构化内容，不负责样式
2. **提高生成速度** - 模板布局预定义，只需填充内容
3. **确保模板质量** - 专业设计的模板布局，保证美观性

## 架构设计

```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│  Template       │     │  AI Service      │     │  Template       │
│  Designer       │────▶│  (DeepSeek)      │────▶│  Preview        │
│  Service        │     │                  │     │  Service        │
│                 │     │  生成结构化内容   │     │  生成预览图      │
│  定义模板布局    │     │                  │     │                 │
└─────────────────┘     └──────────────────┘     └─────────────────┘
         │                        │                        │
         └────────────────────────┼────────────────────────┘
                                  │
                                  ▼
                        ┌─────────────────┐
                        │  Template       │
                        │  Service        │
                        │                 │
                        │  模板 + 内容合成   │
                        └─────────────────┘
```

## 核心组件

### 1. TemplateDesignerService

**位置**: `backend/src/main/java/com/pptai/service/TemplateDesignerService.java`

**功能**: 创建和管理 PPT 模板布局

**支持的模板风格**:
- `generateBusinessTemplate()` - 商务风格（6 种布局）
- `generateTechTemplate()` - 科技风格（4 种布局）
- `generateEducationTemplate()` - 教育风格（5 种布局）
- `generateMinimalTemplate()` - 简约风格（3 种布局）

**布局类型**:
- `title` - 封面页
- `content` - 内容页
- `image-content` - 图文页
- `list` - 列表页
- `section` - 章节页
- `end` - 结束页

### 2. TemplateLayout DTO

**位置**: `backend/src/main/java/com/pptai/dto/template/TemplateLayout.java`

**结构**:

```java
{
  "layoutType": "title",
  "name": "封面页",
  "backgroundColor": "#667eea",
  "gradient": {
    "type": "linear",
    "direction": "45deg",
    "colors": ["#3a7bd5", "#00d2ff"]
  },
  "elements": [
    {
      "type": "title",
      "position": "center",
      "fontSize": 48,
      "color": "#ffffff",
      "placeholderKey": "title",
      "required": true
    }
  ]
}
```

### 3. TemplatePreviewService

**位置**: `backend/src/main/java/com/pptai/service/TemplatePreviewService.java`

**功能**: 生成模板布局的预览图（Base64 PNG 格式）

**使用方法**:
```java
String previewBase64 = templatePreviewService.generatePreview(layouts);
```

### 4. TemplateService

**位置**: `backend/src/main/java/com/pptai/service/TemplateService.java`

**核心方法**:
- `applyTemplate(PPTProject project, List<Slide> aiGeneratedSlides)` - 将模板样式应用到 AI 生成的幻灯片
- `generatePresetTemplates()` - 批量生成预定义模板

## API 接口

### 获取模板列表
```http
GET /api/templates
```

### 获取模板详情
```http
GET /api/templates/{id}
```

### 获取模板布局
```http
GET /api/templates/layouts/{style}
```

参数:
- `style`: 模板风格（business, tech, education, minimal）

### 获取模板风格列表
```http
GET /api/templates/styles
```

### 初始化预定义模板
```http
POST /api/templates/init-presets
```

### 收藏/取消收藏模板
```http
POST /api/templates/{id}/favorite
DELETE /api/templates/{id}/favorite
```

## 前端使用

### 模板列表页面

**位置**: `frontend/src/views/templates/TemplateList.vue`

**功能**:
- 模板列表展示
- 风格选择器
- 布局预览
- 收藏管理

### 类型定义

**位置**: `frontend/src/types/template.ts`

```typescript
interface TemplateLayout {
  layoutType: string
  name: string
  backgroundColor?: string
  gradient?: GradientConfig
  elements: LayoutElement[]
}

interface GradientConfig {
  type: string
  direction: string
  colors: string[]
}

interface LayoutElement {
  type: string
  position: string
  fontSize?: number
  color?: string
  placeholderKey?: string
  required?: boolean
}
```

## 预定义模板

### 商务风格

包含 6 种布局：
1. 封面页 - 渐变蓝色背景，大标题 + 副标题
2. 目录页 - 白色背景，标题 + 列表
3. 内容页 - 白色背景，标题 + 正文
4. 图文页 - 浅灰背景，标题 + 图片 + 内容
5. 列表页 - 白色背景，标题 + 编号列表
6. 结束页 - 紫色背景，致谢 + 联系信息

### 科技风格

包含 4 种布局：
1. 科技封面 - 深蓝渐变背景，标题 + Logo
2. 章节页 - 深色背景，章节标题 + 图标
3. 内容页 - 深色背景，标题 + 内容 + 页脚
4. 数据页 - 蓝色背景，标题 + 图表 + 描述

### 教育风格

包含 5 种布局：
1. 教育封面 - 绿色渐变背景，标题 + 图标
2. 课程目录 - 白色背景，标题 + 编号列表
3. 知识点 - 浅绿背景，标题 + 内容 + 引用
4. 图文教学 - 白色背景，标题 + 图片 + 内容
5. 课程总结 - 绿色背景，总结 + 教师信息

### 简约风格

包含 3 种布局：
1. 简约封面 - 白色背景，大标题 + 副标题
2. 纯文本页 - 白色背景，标题 + 内容
3. 大图展示 - 全背景图片，底部标题

## 初始化模板

系统首次启动时，调用以下 API 生成预定义模板：

```bash
curl -X POST http://localhost:8080/api/templates/init-presets
```

这会在数据库中创建 4 个预定义模板（商务、科技、教育、简约各一个）。

## 测试

### 运行单元测试

```bash
cd backend
mvn test -Dtest=TemplateDesignerServiceTest
mvn test -Dtest=TemplatePreviewServiceTest
```

### 测试覆盖率

两个测试类覆盖了：
- TemplateDesignerService 的所有生成方法
- TemplatePreviewService 的预览生成功能
- 布局和元素的有效性验证
- Base64 格式验证

## 扩展示例

### 添加新的模板风格

1. 在 `TemplateDesignerService` 中添加生成方法：

```java
public List<TemplateLayout> generateMedicalTemplate() {
    List<TemplateLayout> layouts = new ArrayList<>();
    
    layouts.add(TemplateLayout.builder()
        .layoutType("title")
        .name("医疗封面")
        .backgroundColor("#00b4db")
        .elements(List.of(
            createTitleElement("center", 48, "#ffffff", "title"),
            createSubtitleElement("bottom", 24, "#ffffff", "subtitle")
        ))
        .build());
    
    // ... 更多布局
    
    log.info("生成医疗模板布局，共{}种", layouts.size());
    return layouts;
}
```

2. 在 `TemplateController` 中添加路由：

```java
@GetMapping("/layouts/{style}")
public ResponseEntity<ApiResponse<List<TemplateLayout>>> getTemplateLayouts(
        @PathVariable String style) {
    
    List<TemplateLayout> layouts = switch (style.toLowerCase()) {
        case "medical" -> templateDesignerService.generateMedicalTemplate();
        // ... 其他风格
        default -> throw new IllegalArgumentException("不支持的模板风格：" + style);
    };
    
    return ResponseEntity.ok(ApiResponse.success(layouts));
}
```

3. 更新前端的风格选择器

## 注意事项

1. **模板与内容的分离**：模板只定义视觉样式，AI 只生成结构化内容
2. **占位符键**：每个元素应该设置 `placeholderKey`，用于内容填充
3. **必需元素**：关键元素应设置 `required: true`
4. **预览图生成**：使用 Java AWT 生成预览图，确保颜色对比度足够
5. **布局循环使用**：当幻灯片数量超过模板布局数量时，循环使用布局

## 故障排查

### 预览图显示异常
- 检查 `TemplatePreviewService` 的颜色解析是否正确
- 确保十六进制颜色格式正确（#RRGGBB）

### 内容填充失败
- 检查 `placeholderKey` 是否匹配 AI 生成的内容字段
- 验证 `applyTemplateToSlide` 方法的逻辑

### 模板加载失败
- 确保 `generatePresetTemplates()` 已执行
- 检查数据库 templates 表是否存在

## 性能优化

1. **预览图缓存**：生成的预览图可以缓存到 Redis
2. **布局定义缓存**：模板布局可以序列化为 JSON 存储在数据库中
3. **懒加载**：只在选择模板详情时加载布局预览

## 后续规划

1. **自定义模板设计器** - 前端可视化编辑模板布局
2. **模板市场** - 用户上传和分享模板
3. **AI 推荐模板** - 根据主题智能推荐模板风格
4. **更多模板风格** - 医疗、金融、艺术等垂直领域模板
