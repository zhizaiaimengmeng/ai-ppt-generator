# AI PPT Generator 项目文档

## 项目概述

AI PPT Generator 是一个基于 Web 的智能演示文稿生成平台，采用前后端分离架构。系统通过 AI 技术自动生成高质量的 PPT 演示文稿，支持多种生成方式、丰富的模板库和多种输出格式导出。

**创建时间**: 2026-04-16  
**技术栈**: Vue 3 + Spring Boot 3 + MySQL + Redis

## 快速开始

### 环境要求
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- Maven 3.6+

### 启动步骤

1. **启动数据库和中间件**
   ```bash
   cd backend
   docker-compose up -d
   ```

2. **启动后端服务**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

3. **启动前端服务**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## 目录结构

```
/workspace/
├── backend/                    # Spring Boot 后端项目
│   ├── src/main/java/com/pptai/
│   │   ├── entity/            # 实体类
│   │   ├── repository/        # 数据访问层
│   │   ├── service/           # 业务逻辑层
│   │   ├── controller/        # REST API 控制器
│   │   ├── config/            # 配置类
│   │   ├── dto/               # 数据传输对象
│   │   ├── exception/         # 异常处理
│   │   └── util/              # 工具类
│   └── src/main/resources/
│       ├── application.yml    # 应用配置
│       └── mysql-init/        # 数据库初始化脚本
├── frontend/                   # Vue 3 前端项目
│   ├── src/
│   │   ├── api/               # API 请求封装
│   │   ├── assets/            # 静态资源
│   │   ├── components/        # 组件
│   │   ├── layouts/           # 布局组件
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # Pinia 状态管理
│   │   ├── styles/            # 样式文件
│   │   ├── types/             # TypeScript 类型定义
│   │   ├── utils/             # 工具函数
│   │   ├── views/             # 页面视图
│   │   ├── App.vue            # 根组件
│   │   └── main.ts            # 入口文件
│   └── public/                # 公共资源
├── .monkeycode/
│   ├── docs/                  # 项目文档
│   └── specs/
│       └── ai-ppt-generator/  # 功能规格文档
│           ├── requirements.md
│           ├── design.md
│           └── tasklist.md
└── README.md
```

## 核心功能模块

### 1. 认证模块 (Auth)
- 用户注册与登录
- JWT Token 认证
- 邮箱验证
- 密码重置

### 2. PPT 生成模块 (PPT Generation)
- 主题驱动生成
- 网络内容聚合生成
- AI 内容生成
- 进度跟踪

### 3. 模板模块 (Template)
- 模板库管理
- 模板分类
- 模板收藏

### 4. 项目管理模块 (Project)
- 项目 CRUD
- 项目列表
- 项目编辑器

### 5. 导出模块 (Export)
- PPTX 导出
- PDF 导出
- 在线分享链接
- 长图导出

## 数据库设计

### 核心表

| 表名 | 说明 |
|------|------|
| users | 用户表 |
| ppt_projects | PPT 项目表 |
| slides | 幻灯片表 |
| templates | 模板表 |
| export_records | 导出记录表 |
| user_favorites | 用户收藏表 |

详细表结构见 [数据库设计](#数据库设计) 章节。

## API 接口

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/verify-email` - 邮箱验证
- `POST /api/auth/forgot-password` - 忘记密码
- `POST /api/auth/reset-password` - 重置密码

### PPT 接口
- `POST /api/ppt/generate` - 生成 PPT
- `GET /api/ppt/generation/{id}/status` - 获取生成进度
- `GET /api/ppt/{id}` - 获取 PPT 详情
- `PUT /api/ppt/{id}` - 更新 PPT

### 模板接口
- `GET /api/templates` - 获取模板列表
- `GET /api/templates/{id}` - 获取模板详情
- `POST /api/templates/{id}/favorite` - 收藏模板

### 导出接口
- `POST /api/export/{id}` - 导出 PPT
- `POST /api/export/{id}/share-link` - 生成分享链接

详见 [API 文档](#API 文档)。

## 开发流程

1. **查看任务列表**: 阅读 `.monkeycode/specs/ai-ppt-generator/tasklist.md`
2. **实现功能**: 按任务列表逐项实现
3. **编写测试**: 确保测试覆盖率达到要求
4. **更新文档**: 使用 `/project-wiki` skill 同步文档
5. **提交代码**: 提交到 Git 仓库

## 配置说明

### 后端配置

后端配置文件位于 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ppt_ai
    username: root
    password: ${MYSQL_PASSWORD:root123}
  
  jpa:
    hibernate:
      ddl-auto: update
  
  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret: ${JWT_SECRET:your-secret-key}
  expiration: 86400000  # 24 hours
```

### 前端配置

前端配置文件位于 `frontend/.env.development`：

```
VITE_API_BASE_URL=/api
VITE_APP_TITLE=AI PPT Generator
```

## 测试

### 后端测试
```bash
mvn test
mvn jacoco:report  # 生成覆盖率报告
```

### 前端测试
```bash
npm run test          # 单元测试
npm run test:e2e      # E2E 测试
npm run test:coverage # 覆盖率报告
```

## 部署

### Docker 部署

```bash
# 后端
cd backend
docker build -t ppt-ai-backend .
docker run -p 8080:8080 ppt-ai-backend

# 前端
cd frontend
docker build -t ppt-ai-frontend .
docker run -p 3000:3000 ppt-ai-frontend
```

## 故障排查

### 常见问题

1. **后端启动失败**
   - 检查 MySQL 是否运行
   - 检查数据库配置是否正确
   - 查看日志：`logs/application.log`

2. **前端无法连接后端**
   - 检查 Docker 容器是否运行
   - 验证反向代理配置
   - 检查 CORS 设置

3. **邮件发送失败**
   - 检查 MailHog 是否运行 (端口 1025)
   - 验证邮件配置

## 相关文档

- [需求文档](../specs/ai-ppt-generator/requirements.md)
- [技术设计](../specs/ai-ppt-generator/design.md)
- [任务列表](../specs/ai-ppt-generator/tasklist.md)

## 更新日志

### 2026-04-16 ✓ 完成
- ✅ 完成项目初始化
- ✅ 搭建后端 Spring Boot 框架
- ✅ 搭建前端 Vue 3 框架
- ✅ 创建基础页面 (登录、注册)
- ✅ 配置 Docker 开发环境
- ✅ 实现数据库实体层 (User, PPTProject, Slide, Template, ExportRecord)
- ✅ 实现 Repository 层
- ✅ 实现认证模块 (Spring Security + JWT)
- ✅ 实现邮箱验证和密码重置功能
- ✅ 创建单元测试

### 开发中
- 🔲 实现 PPT 生成模块 (待开始)
- 🔲 实现模板管理 (待开始)
- 🔲 实现导出功能 (待开始)
- 🔲 实现在线编辑器 (待开始)

---

文档最后更新：2026-04-16
