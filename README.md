# AI PPT Generator

AI 驱动的在线 PPT 生成工具，快速创建专业演示文稿。

## 技术栈

### 后端
- Spring Boot 3.2.4
- Spring Data JPA
- MySQL 8.0
- Redis
- Spring Security + JWT
- Apache POI (PPTX 生成)
- iText 7 (PDF 生成)
- DeepSeek AI (内容生成)
- Maven

### 前端
- Vue 3 + TypeScript
- Vite 5
- Element Plus
- Pinia (状态管理)
- Vue Router
- Axios
- Vitest + Cypress (测试)

## 快速开始

### 环境要求
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- Maven 3.6+

### 1. 启动数据库和中间件

```bash
cd backend
docker-compose up -d
```

这将启动：
- MySQL (端口 3306)
- Redis (端口 6379)
- MailHog (端口 1025 SMTP, 8025 Web UI) - 开发环境邮件捕获

### 2. 启动后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 http://localhost:3000 启动

## 项目结构

### 后端目录

```
backend/
├── src/main/java/com/pptai/
│   ├── entity/          # 实体类
│   ├── repository/      # 数据访问层
│   ├── service/         # 业务逻辑层
│   ├── controller/      # REST API 控制器
│   ├── config/          # 配置类
│   ├── dto/             # 数据传输对象
│   ├── exception/       # 异常处理
│   └── util/            # 工具类
└── src/main/resources/
    ├── application.yml  # 应用配置
    └── mysql-init/      # 数据库初始化脚本
```

### 前端目录

```
frontend/
├── src/
│   ├── api/             # API 请求
│   ├── assets/          # 静态资源
│   ├── components/      # 组件
│   ├── layouts/         # 布局组件
│   ├── router/          # 路由配置
│   ├── stores/          # Pinia 状态管理
│   ├── styles/          # 样式
│   ├── types/           # TypeScript 类型
│   ├── utils/           # 工具函数
│   ├── views/           # 页面视图
│   ├── App.vue          # 根组件
│   └── main.ts          # 入口文件
├── index.html
├── package.json
├── vite.config.ts
└── tsconfig.json
```

## API 文档

启动后端后，访问 Swagger UI:
http://localhost:8080/swagger-ui.html

## 开发指南

### 后端

```bash
# 运行测试
mvn test

# 打包
mvn clean package

# 查看覆盖率
mvn jacoco:report
```

### 前端

```bash
# 运行测试
npm run test

# E2E 测试
npm run test:e2e

# 打包
npm run build

# 代码检查
npm run lint
```

## 功能特性

- ✅ 用户注册与登录
- ✅ 主题驱动 PPT 生成
- ✅ 网络内容聚合生成
- 🔲 丰富模板库 (开发中)
- 🔲 在线编辑器 (开发中)
- 🔲 多种格式导出 (开发中)
- 🔲 分享链接生成 (开发中)

## License

MIT
