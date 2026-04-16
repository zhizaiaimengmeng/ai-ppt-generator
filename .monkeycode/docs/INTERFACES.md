# 接口文档

## API 接口规范

### 基础信息

- **Base URL**: `/api`
- **数据格式**: JSON
- **认证方式**: JWT Bearer Token
- **字符编码**: UTF-8

### 统一响应格式

```json
{
  "code": "00000",
  "message": "操作成功",
  "data": {},
  "timestamp": 1681689600000
}
```

### 错误码规范

| 错误码前缀 | 说明 |
|-----------|------|
| 00000 | 成功 |
| 40000 | 参数错误 |
| 40100 | 认证错误 |
| 40400 | 资源不存在 |
| 42900 | 频率限制 |
| 50000 | 服务器错误 |
| 51000 | AI 服务错误 |
| 52000 | 导出服务错误 |
| 53000 | 文件服务错误 |

## 认证接口

### 1. 用户注册

**POST** `/api/auth/register`

**请求参数**:

```json
{
  "email": "user@example.com",
  "password": "password123",
  "confirmPassword": "password123"
}
```

**响应示例**:

```json
{
  "code": "00000",
  "message": "注册成功",
  "data": {
    "userId": 1
  },
  "timestamp": 1681689600000
}
```

### 2. 用户登录

**POST** `/api/auth/login`

**请求参数**:

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**响应示例**:

```json
{
  "code": "00000",
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...",
    "user": {
      "id": 1,
      "email": "user@example.com",
      "nickname": "用户昵称",
      "avatarUrl": "https://...",
      "emailVerified": true,
      "createdAt": "2026-04-16T00:00:00Z"
    }
  },
  "timestamp": 1681689600000
}
```

### 3. 用户登出

**POST** `/api/auth/logout`

**请求头**:

```
Authorization: Bearer <token>
```

**响应示例**:

```json
{
  "code": "00000",
  "message": "登出成功",
  "data": null,
  "timestamp": 1681689600000
}
```

### 4. 发送邮箱验证码

**POST** `/api/auth/send-verification-code`

**请求参数**:

```json
{
  "email": "user@example.com"
}
```

### 5. 验证邮箱

**GET** `/api/auth/verify-email`

**请求参数**:

```
Query: token={验证令牌}
```

### 6. 忘记密码

**POST** `/api/auth/forgot-password`

**请求参数**:

```json
{
  "email": "user@example.com"
}
```

### 7. 重置密码

**POST** `/api/auth/reset-password`

**请求参数**:

```json
{
  "token": "重置令牌",
  "newPassword": "newpassword123"
}
```

### 8. 获取当前用户信息

**GET** `/api/auth/me`

**请求头**:

```
Authorization: Bearer <token>
```

**响应示例**:

```json
{
  "code": "00000",
  "message": "操作成功",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "nickname": "用户昵称",
    "avatarUrl": "https://...",
    "emailVerified": true,
    "createdAt": "2026-04-16T00:00:00Z"
  },
  "timestamp": 1681689600000
}
```

## PPT 接口

### 1. 生成 PPT

**POST** `/api/ppt/generate`

**请求参数**:

```json
{
  "mode": "topic",
  "topic": "人工智能技术应用",
  "templateId": 1,
  "options": {
    "slideCount": 10,
    "includeReferences": true
  }
}
```

**响应示例**:

```json
{
  "code": "00000",
  "message": "开始生成",
  "data": {
    "projectId": 1,
    "status": "pending",
    "estimatedTime": 60
  },
  "timestamp": 1681689600000
}
```

### 2. 获取生成进度

**GET** `/api/ppt/generation/{projectId}/status`

**路径参数**:

```
projectId: 1
```

**响应示例**:

```json
{
  "code": "00000",
  "message": "操作成功",
  "data": {
    "status": "processing",
    "progress": 50,
    "message": "正在生成幻灯片内容..."
  },
  "timestamp": 1681689600000
}
```

### 3. 获取 PPT 详情

**GET** `/api/ppt/{projectId}`

**路径参数**:

```
projectId: 1
```

**响应示例**:

```json
{
  "code": "00000",
  "message": "操作成功",
  "data": {
    "id": 1,
    "userId": 1,
    "title": "人工智能技术应用",
    "templateId": 1,
    "generationMode": "topic",
    "status": "completed",
    "generationProgress": 100,
    "createdAt": "2026-04-16T00:00:00Z",
    "updatedAt": "2026-04-16T00:01:00Z",
    "slides": [
      {
        "id": 1,
        "slideNumber": 1,
        "layoutType": "title",
        "title": "人工智能技术应用",
        "content": {
          "subtitle": "探索 AI 的未来"
        }
      }
    ]
  },
  "timestamp": 1681689600000
}
```

### 4. 更新 PPT

**PUT** `/api/ppt/{projectId}`

**请求参数**:

```json
{
  "title": "新的标题",
  "slides": [
    {
      "id": 1,
      "title": "更新后的标题",
      "content": {}
    }
  ]
}
```

### 5. 管理幻灯片

**POST** `/api/ppt/{projectId}/slides` - 添加幻灯片  
**PUT** `/api/ppt/{projectId}/slides/{slideId}` - 更新幻灯片  
**DELETE** `/api/ppt/{projectId}/slides/{slideId}` - 删除幻灯片

## 模板接口

### 1. 获取模板列表

**GET** `/api/templates`

**查询参数**:

```
category: 商务
keyword: 简洁
page: 1
size: 20
```

**响应示例**:

```json
{
  "code": "00000",
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "商务模板 - 简洁",
        "category": "商务",
        "description": "简洁专业的商务模板",
        "previewUrl": "https://...",
        "isPremium": false,
        "downloadCount": 100,
        "favoriteCount": 50
      }
    ],
    "total": 50,
    "page": 1,
    "size": 20
  },
  "timestamp": 1681689600000
}
```

### 2. 获取模板详情

**GET** `/api/templates/{id}`

**路径参数**:

```
id: 1
```

### 3. 收藏模板

**POST** `/api/templates/{id}/favorite`

## 项目接口

### 1. 获取项目列表

**GET** `/api/projects`

**查询参数**:

```
page: 1
size: 20
keyword: 关键词
```

### 2. 创建项目

**POST** `/api/projects`

**请求参数**:

```json
{
  "title": "项目名称",
  "content": {}
}
```

### 3. 更新项目

**PUT** `/api/projects/{id}`

### 4. 删除项目

**DELETE** `/api/projects/{id}`

### 5. 复制项目

**POST** `/api/projects/{id}/duplicate`

## 导出接口

### 1. 导出 PPT

**POST** `/api/export/{projectId}`

**请求参数**:

```json
{
  "format": "pptx"
}
```

**响应示例**:

```json
{
  "code": "00000",
  "message": "开始导出",
  "data": {
    "exportId": 1,
    "status": "pending",
    "downloadUrl": null
  },
  "timestamp": 1681689600000
}
```

### 2. 获取导出状态

**GET** `/api/export/{exportId}/status`

### 3. 生成分享链接

**POST** `/api/export/{projectId}/share-link`

**请求参数**:

```json
{
  "password": "可选密码",
  "expirationDays": 7
}
```

**响应示例**:

```json
{
  "code": "00000",
  "message": "操作成功",
  "data": {
    "shareUrl": "https://ppt-ai.com/share/abc123",
    "expirationTime": "2026-04-23T00:00:00Z"
  },
  "timestamp": 1681689600000
}
```

## 数据库表结构

### users 表

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| email | VARCHAR(255) | 邮箱 | UNIQUE, NOT NULL |
| password_hash | VARCHAR(255) | 密码哈希 | NOT NULL |
| nickname | VARCHAR(100) | 昵称 | - |
| avatar_url | VARCHAR(500) | 头像 URL | - |
| email_verified | BOOLEAN | 邮箱验证状态 | DEFAULT FALSE |
| verification_token | VARCHAR(255) | 验证令牌 | - |
| reset_password_token | VARCHAR(255) | 重置令牌 | - |
| reset_password_expires | DATETIME | 重置过期时间 | - |
| created_at | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| updated_at | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE |

### ppt_projects 表

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 用户 ID | FOREIGN KEY, NOT NULL |
| title | VARCHAR(500) | 标题 | NOT NULL |
| template_id | BIGINT | 模板 ID | FOREIGN KEY |
| generation_mode | VARCHAR(50) | 生成模式 | - |
| status | VARCHAR(50) | 状态 | DEFAULT 'draft' |
| generation_progress | INT | 生成进度 | DEFAULT 0 |
| generation_message | VARCHAR(500) | 生成消息 | - |
| created_at | DATETIME | 创建时间 | - |
| updated_at | DATETIME | 更新时间 | - |

### slides 表

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| project_id | BIGINT | 项目 ID | FOREIGN KEY, NOT NULL |
| slide_number | INT | 幻灯片编号 | NOT NULL |
| layout_type | VARCHAR(50) | 布局类型 | - |
| title | VARCHAR(500) | 标题 | - |
| content | JSON | 内容 | - |
| background_color | VARCHAR(50) | 背景色 | - |
| created_at | DATETIME | 创建时间 | - |
| updated_at | DATETIME | 更新时间 | - |

### templates 表

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(200) | 模板名称 | NOT NULL |
| category | VARCHAR(100) | 分类 | - |
| description | TEXT | 描述 | - |
| preview_url | VARCHAR(500) | 预览 URL | - |
| template_file_url | VARCHAR(500) | 模板文件 URL | - |
| color_scheme | JSON | 配色方案 | - |
| font_scheme | JSON | 字体方案 | - |
| slide_layouts | JSON | 幻灯片布局 | - |
| is_premium | BOOLEAN | 是否付费 | DEFAULT FALSE |
| download_count | INT | 下载次数 | DEFAULT 0 |
| favorite_count | INT | 收藏次数 | DEFAULT 0 |
| created_at | DATETIME | 创建时间 | - |
| updated_at | DATETIME | 更新时间 | - |

### export_records 表

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 用户 ID | FOREIGN KEY, NOT NULL |
| project_id | BIGINT | 项目 ID | FOREIGN KEY, NOT NULL |
| export_format | VARCHAR(50) | 导出格式 | NOT NULL |
| file_url | VARCHAR(500) | 文件 URL | - |
| file_size | BIGINT | 文件大小 | - |
| status | VARCHAR(50) | 状态 | DEFAULT 'pending' |
| progress | INT | 进度 | DEFAULT 0 |
| error_message | VARCHAR(500) | 错误消息 | - |
| share_url | VARCHAR(500) | 分享 URL | - |
| share_password_hash | VARCHAR(255) | 分享密码 | - |
| share_expires_at | DATETIME | 分享过期时间 | - |
| download_count | INT | 下载次数 | DEFAULT 0 |
| created_at | DATETIME | 创建时间 | - |
| updated_at | DATETIME | 更新时间 | - |

### user_favorites 表

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 用户 ID | FOREIGN KEY, NOT NULL |
| template_id | BIGINT | 模板 ID | FOREIGN KEY, NOT NULL |
| created_at | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |

**唯一约束**: UNIQUE KEY unique_user_template (user_id, template_id)

---

文档最后更新：2026-04-16
