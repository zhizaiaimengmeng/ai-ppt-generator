# AI 生成 PPT 应用 - 实施任务列表

Feature Name: ai-ppt-generator
Created: 2026-04-16

## Phase 1: 项目初始化

### 1.1 后端项目搭建
- [x] 1.1.1 使用 Spring Initializr 创建 Spring Boot 3 + Maven 项目
- [x] 1.1.2 配置项目结构和包结构 (com.pptai.*)
- [x] 1.1.3 添加依赖：Spring Web, Spring Data JPA, MySQL Connector, Redis, Lombok, Validation
- [x] 1.1.4 配置 application.yml 开发环境和测试环境
- [x] 1.1.5 配置统一错误处理和响应格式

### 1.2 前端项目搭建
- [x] 1.2.1 使用 Vite 创建 Vue 3 + TypeScript 项目
- [x] 1.2.2 添加依赖：Vue Router, Pinia, Element Plus, Axios
- [x] 1.2.3 配置前端反向代理 (vite.config.ts 配置/api 代理到后端)
- [x] 1.2.4 配置 ESLint 和 Prettier 代码规范
- [x] 1.2.5 创建基础目录结构 (views, components, router, store, api, utils)

### 1.3 开发环境配置
- [x] 1.3.1 创建 Docker Compose 配置文件 (MySQL 8.0, Redis)
- [x] 1.3.2 创建数据库初始化脚本
- [x] 1.3.3 配置 Git 仓库和.gitignore
- [x] 1.3.4 编写 README.md 项目说明文档

## Phase 2: 数据库和实体层

### 2.1 数据库表创建
- [x] 2.1.1 创建 users 表
- [x] 2.1.2 创建 ppt_projects 表
- [x] 2.1.3 创建 slides 表
- [x] 2.1.4 创建 templates 表
- [x] 2.1.5 创建 export_records 表
- [x] 2.1.6 创建 user_favorites 表
- [x] 2.1.7 创建索引和外键约束

### 2.2 实体类实现
- [x] 2.2.1 创建 User 实体类
- [x] 2.2.2 创建 PPTProject 实体类
- [x] 2.2.3 创建 Slide 实体类
- [x] 2.2.4 创建 Template 实体类
- [x] 2.2.5 创建 ExportRecord 实体类
- [x] 2.2.6 添加实体关系映射 (@OneToMany, @ManyToOne, @ManyToMany)

### 2.3 Repository 层
- [x] 2.3.1 创建 UserRepository 接口
- [x] 2.3.2 创建 PPTProjectRepository 接口
- [x] 2.3.3 创建 SlideRepository 接口
- [x] 2.3.4 创建 TemplateRepository 接口
- [x] 2.3.5 创建 ExportRecordRepository 接口
- [x] 2.3.6 添加自定义查询方法

## Phase 3: 认证模块

### 3.1 后端认证服务
- [x] 3.1.1 配置 Spring Security 和 JWT
- [x] 3.1.2 实现密码加密存储 (bcrypt)
- [x] 3.1.3 创建 JwtTokenProvider 工具类
- [x] 3.1.4 实现 AuthService (注册、登录、刷新 token)
- [x] 3.1.5 实现 EmailService (发送验证邮件和重置密码邮件)
- [x] 3.1.6 实现用户注册接口 (POST /api/auth/register)
- [x] 3.1.7 实现用户登录接口 (POST /api/auth/login)
- [x] 3.1.8 实现邮箱验证接口 (GET /api/auth/verify-email)
- [x] 3.1.9 实现忘记密码接口 (POST /api/auth/forgot-password)
- [x] 3.1.10 实现重置密码接口 (POST /api/auth/reset-password)

### 3.2 前端认证页面
- [x] 3.2.1 实现登录页面 (Login.vue)
- [x] 3.2.2 实现注册页面 (Register.vue)
- [x] 3.2.3 实现忘记密码页面 (ForgotPassword.vue)
- [x] 3.2.4 实现重置密码页面 (ResetPassword.vue)
- [x] 3.2.5 实现用户状态管理 (Vuex/Pinia auth store)
- [x] 3.2.6 实现路由守卫 (需要登录的路由保护)
- [x] 3.2.7 实现邮箱验证页面 (VerifyEmail.vue)

## Phase 4: 模板模块

### 4.1 后端模板服务
- [x] 4.1.1 实现 TemplateService (CRUD 操作)
- [x] 4.1.2 实现模板分类和搜索功能
- [x] 4.1.3 实现模板收藏功能
- [x] 4.1.4 创建初始模板数据 (50+ 模板)
- [x] 4.1.5 实现获取模板列表接口 (GET /api/templates)
- [x] 4.1.6 实现获取模板详情接口 (GET /api/templates/{id})
- [x] 4.1.7 实现收藏模板接口 (POST /api/templates/{id}/favorite)
- [x] 4.1.8 实现模板文件上传接口 (POST /api/templates/upload)

### 4.2 前端模板页面
- [x] 4.2.1 实现模板列表页面 (TemplateList.vue)
- [x] 4.2.2 实现 TemplateCard 组件
- [x] 4.2.3 实现模板搜索和筛选功能
- [x] 4.2.4 实现模板预览对话框
- [x] 4.2.5 实现模板收藏功能 UI

## Phase 5: PPT 生成核心功能

### 5.1 AI 服务集成
- [x] 5.1.1 实现 AIService 接口
- [x] 5.1.2 配置 AI 大模型 API 连接
- [x] 5.1.3 实现主题驱动生成逻辑 (调用 AI 生成大纲和内容)
- [x] 5.1.4 实现网络内容聚合逻辑 (调用搜索 API+AI 总结)
- [x] 5.1.5 实现 AI 生成结果解析和格式化

### 5.2 PPT 生成服务
- [x] 5.2.1 实现 PPTGenerationService
- [x] 5.2.2 实现异步生成任务管理
- [x] 5.2.3 实现生成进度跟踪
- [x] 5.2.4 实现生成失败重试机制
- [x] 5.2.5 实现 PPT 生成接口 (POST /api/ppt/generate)
- [x] 5.2.6 实现生成进度查询接口 (GET /api/ppt/generation/{id}/status)

### 5.3 前端PPT创建页面
- [x] 5.3.1 实现创建 PPT 主页面 (CreatePPT.vue)
- [x] 5.3.2 实现生成方式选择组件
- [x] 5.3.3 实现主题输入组件
- [x] 5.3.4 实现模板选择集成
- [x] 5.3.5 实现生成进度显示组件
- [x] 5.3.6 实现生成结果预览

## Phase 6: PPT 编辑器

### 6.1 后端编辑功能
- [x] 6.1.1 实现 ProjectService (项目 CRUD)
- [x] 6.1.2 实现幻灯片管理 (添加/删除/移动)
- [x] 6.1.3 实现自动保存逻辑
- [x] 6.1.4 实现获取 PPT 详情接口 (GET /api/ppt/{projectId})
- [x] 6.1.5 实现更新 PPT 接口 (PUT /api/ppt/{projectId})
- [x] 6.1.6 实现管理幻灯片接口 (POST/PUT/DELETE /api/ppt/{projectId}/slides)

### 6.2 前端编辑器
- [x] 6.2.1 实现项目编辑器页面 (ProjectEditor.vue)
- [x] 6.2.2 实现 PPTPreview 组件
- [x] 6.2.3 实现 SlideThumbnail 组件
- [x] 6.2.4 实现幻灯片编辑功能 (文本编辑)
- [x] 6.2.5 实现工具栏 (添加/删除/复制/移动幻灯片)
- [x] 6.2.6 实现自动保存功能
- [x] 6.2.7 实现模板切换功能

## Phase 7: 导出模块

### 7.1 PPTX 导出
- [x] 7.1.1 添加 Apache POI 依赖
- [x] 7.1.2 实现 PPTXExportService
- [x] 7.1.3 实现 PPTX 文件生成逻辑
- [x] 7.1.4 实现文件上传到对象存储
- [x] 7.1.5 实现导出接口 (POST /api/export/{projectId})

### 7.2 PDF 导出
- [x] 7.2.1 添加 PDF 导出库 (iText 或 Apache PDFBox)
- [x] 7.2.2 实现 PDFExportService
- [x] 7.2.3 实现 PDF 文件生成逻辑
- [x] 7.2.4 支持 A4 和 16:9 比例

### 7.3 在线分享链接
- [x] 7.3.1 实现 ShareLinkService
- [x] 7.3.2 生成唯一分享 URL
- [x] 7.3.3 实现分享链接访问控制 (密码和有效期)
- [x] 7.3.4 创建公开访问页面 (Web 版 PPT 演示)
- [x] 7.3.5 实现分享链接接口 (POST /api/export/{projectId}/share-link)

### 7.4 长图导出
- [x] 7.4.1 添加图片处理库
- [x] 7.4.2 实现 ImageExportService
- [x] 7.4.3 实现幻灯片拼接成长图逻辑
- [x] 7.4.4 生成 PNG 格式图片

### 7.5 导出管理
- [x] 7.5.1 实现 ExportService
- [x] 7.5.2 实现导出进度跟踪
- [x] 7.5.3 实现导出状态查询接口 (GET /api/export/{exportId}/status)
- [x] 7.5.4 实现导出记录管理

### 7.6 前端导出功能
- [x] 7.6.1 实现 ExportDialog 组件
- [x] 7.6.2 实现导出格式选择
- [x] 7.6.3 实现 ExportProgress 组件
- [x] 7.6.4 实现导出完成下载

## Phase 8: 项目管理模块

### 8.1 后端项目服务
- [x] 8.1.1 完善 ProjectService
- [x] 8.1.2 实现项目列表接口 (GET /api/projects)
- [x] 8.1.3 实现创建项目接口 (POST /api/projects)
- [x] 8.1.4 实现更新项目接口 (PUT /api/projects/{id})
- [x] 8.1.5 实现删除项目接口 (DELETE /api/projects/{id})
- [x] 8.1.6 实现复制项目接口 (POST /api/projects/{id}/duplicate)

### 8.2 前端项目页面
- [ ] 8.2.1 实现项目列表页面 (ProjectList.vue)
- [ ] 8.2.2 实现项目卡片组件
- [ ] 8.2.3 实现项目详情页面 (ProjectDetail.vue)
- [ ] 8.2.4 实现项目管理功能 (重命名、删除、复制)
- [ ] 8.2.5 实现项目搜索和筛选

## Phase 9: 模板设计器增强

### 9.1 模板布局定义
- [x] 9.1.1 创建 TemplateLayout DTO
- [x] 9.1.2 完善 TemplateDesignerService（商务、科技、教育、简约风格）
- [x] 9.1.3 在 TemplateService 中实现模板与 AI 内容的合成逻辑
- [x] 9.1.4 创建模板布局 API 端点
- [x] 9.1.5 Slide 实体增加背景渐变和图片支持

### 9.2 模板预览
- [x] 9.2.1 实现模板预览图生成功能 (TemplatePreviewService)
- [x] 9.2.2 创建预定义模板生成（商务、科技、教育、简约风格）
- [x] 9.2.3 前端模板列表增强（支持风格选择器和布局预览）

### 9.3 测试
- [x] 9.3.1 编写 TemplateDesignerService 单元测试
- [x] 9.3.2 编写 TemplatePreviewService 单元测试

## Phase 10: 用户界面优化

### 9.1 布局组件
- [ ] 9.1.1 实现 MainLayout.vue (主布局)
- [ ] 9.1.2 实现 AuthLayout.vue (认证页布局)
- [ ] 9.1.3 实现导航栏组件
- [ ] 9.1.4 实现侧边栏菜单
- [ ] 9.1.5 实现用户头像菜单

### 9.2 通用组件
- [ ] 9.2.1 实现 LoadingSpinner 组件
- [ ] 9.2.2 实现 ErrorBoundary 组件
- [ ] 9.2.3 实现 ConfirmDialog 组件
- [ ] 9.2.4 实现空状态组件
- [ ] 9.2.5 实现消息提示组件

### 9.3 响应式适配
- [ ] 9.3.1 配置移动端适配样式
- [ ] 9.3.2 优化平板设备显示
- [ ] 9.3.3 实现移动端简化编辑器
- [ ] 9.3.4 测试各浏览器兼容性

## Phase 10: 测试和部署

### 10.1 后端测试
- [ ] 10.1.1 编写 Service 层单元测试
- [ ] 10.1.2 编写 Controller 层集成测试
- [ ] 10.1.3 编写 Repository 层测试
- [ ] 10.1.4 配置 Jacoco 代码覆盖率
- [ ] 10.1.5 确保测试覆盖率达到 80%

### 10.2 前端测试
- [ ] 10.2.1 配置 Vitest 单元测试框架
- [ ] 10.2.2 编写组件单元测试
- [ ] 10.2.3 配置 Cypress E2E 测试
- [ ] 10.2.4 编写关键流程 E2E 测试
- [ ] 10.2.5 确保测试通过

### 10.3 性能测试
- [ ] 10.3.1 配置 JMeter 或 Gatling
- [ ] 10.3.2 执行负载测试
- [ ] 10.3.3 优化性能瓶颈

### 10.4 部署配置
- [ ] 10.4.1 配置生产环境 application-prod.yml
- [ ] 10.4.2 创建 Dockerfile (后端)
- [ ] 10.4.3 创建 Dockerfile (前端)
- [ ] 10.4.4 配置 docker-compose.prod.yml
- [ ] 10.4.5 配置 Nginx 反向代理
- [ ] 10.4.6 配置 CI/CD 流程 (.github/workflows/ci.yml)

## Phase 11: 文档和完善

### 11.1 项目文档
- [ ] 11.1.1 完善 README.md (项目介绍、快速开始)
- [ ] 11.1.2 创建 API 文档 (使用 Swagger/OpenAPI)
- [ ] 11.1.3 创建开发者指南
- [ ] 11.1.4 创建部署文档

### 11.2 优化和修复
- [ ] 11.2.1 修复测试中发现的 bug
- [ ] 11.2.2 优化用户体验
- [ ] 11.2.3 优化性能
- [ ] 11.2.4 完善错误提示
- [ ] 11.2.5 进行代码审查和重构

---

**任务状态说明：**
- [ ] 未开始
- [x] 已完成

**注意事项：**
1. 按顺序执行任务，先完成依赖的前置任务
2. 每个任务完成后需要编写相应测试
3. 每个 Phase 完成后进行集成测试
4. 保持代码质量和规范
