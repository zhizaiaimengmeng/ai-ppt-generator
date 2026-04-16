# 开发者指南

## 开发环境配置

### 必需工具

1. **JDK 17+**
   ```bash
   # 验证安装
   java -version
   ```

2. **Maven 3.6+**
   ```bash
   # 验证安装
   mvn -version
   ```

3. **Node.js 18+**
   ```bash
   # 验证安装
   node -v
   npm -v
   ```

4. **Docker & Docker Compose**
   ```bash
   # 验证安装
   docker -v
   docker-compose -v
   ```

5. **Git**
   ```bash
   # 验证安装
   git --version
   ```

### IDE 推荐

- **后端**: IntelliJ IDEA / Eclipse / VS Code
- **前端**: VS Code / WebStorm
- **数据库**: DBeaver / MySQL Workbench

## 项目设置

### 1. 克隆项目

```bash
git clone <repository-url>
cd <project-directory>
```

### 2. 配置后端

```bash
cd backend

# 创建本地配置文件 (可选)
cp src/main/resources/application.yml src/main/resources/application-local.yml

# 编辑配置，修改数据库连接等
```

### 3. 配置前端

```bash
cd frontend

# 创建本地环境文件 (可选)
cp .env.development .env.local

# 编辑配置
```

### 4. 启动依赖服务

```bash
cd backend
docker-compose up -d
```

等待服务启动:
- MySQL: localhost:3306
- Redis: localhost:6379
- MailHog: localhost:8025 (Web UI)

## 后端开发

### Maven 命令

```bash
# 清理编译
mvn clean

# 编译项目
mvn compile

# 运行测试
mvn test

# 打包
mvn package

# 跳过测试打包
mvn package -DskipTests

# 运行应用
mvn spring-boot:run

# 安装到本地仓库
mvn install

# 生成覆盖率报告
mvn jacoco:report
```

### 代码结构

```
src/main/java/com/pptai/
├── PptAiBackendApplication.java  # 主程序入口
├── entity/                        # JPA 实体
│   ├── User.java
│   ├── PPTProject.java
│   └── ...
├── repository/                    # Repository 接口
│   ├── UserRepository.java
│   └── ...
├── service/                       # Service 业务逻辑
│   ├── AuthService.java
│   └── ...
├── controller/                    # Controller REST API
│   ├── AuthController.java
│   └── ...
├── config/                        # 配置类
│   ├── SecurityConfig.java
│   └── ...
├── dto/                           # 数据传输对象
│   ├── ApiResponse.java
│   └── ...
├── exception/                     # 异常处理
│   ├── BusinessException.java
│   ├── GlobalExceptionHandler.java
│   └── ...
└── util/                          # 工具类
    └── ...
```

### 开发规范

1. **命名规范**
   - 类名：CamelCase (如 `UserService`)
   - 方法名：camelCase (如 `getUserById`)
   - 变量名：camelCase (如 `userName`)
   - 常量名：UPPER_SNAKE_CASE (如 `MAX_RETRY_COUNT`)

2. **注释规范**
   ```java
   /**
    * 用户服务类
    * 提供用户相关的业务逻辑处理
    */
   @Service
   public class UserService {
       
       /**
        * 根据 ID 获取用户
        * @param id 用户 ID
        * @return 用户对象
        * @throws UserNotFoundException 当用户不存在时
        */
       public User getUserById(Long id) {
           // ...
       }
   }
   ```

3. **异常处理**
   - 使用自定义异常 `BusinessException`
   - 统一在 `GlobalExceptionHandler` 处理
   - 不要捕获 Exception 后什么都不做

4. **事务管理**
   - Service 层方法使用 `@Transactional`
   - 注意事务传播行为
   - 避免在事务中做耗时操作

### 添加新功能

1. **创建实体类**
   ```bash
   # 在 entity 包创建 JPA 实体
   ```

2. **创建 Repository**
   ```bash
   # 在 repository 包创建接口，继承 JpaRepository
   ```

3. **创建 Service**
   ```bash
   # 在 service 包实现业务逻辑
   ```

4. **创建 Controller**
   ```bash
   # 在 controller 包创建 REST API
   ```

5. **编写测试**
   ```bash
   # 在 test 包编写单元测试和集成测试
   ```

## 前端开发

### NPM 命令

```bash
# 安装依赖
npm install

# 开发模式
npm run dev

# 构建生产版本
npm run build

# 预览生产版本
npm run preview

# 运行测试
npm run test

# E2E 测试
npm run test:e2e

# 代码检查
npm run lint

# 格式化代码
npm run format
```

### 代码结构

```
src/
├── main.ts                    # 应用入口
├── App.vue                    # 根组件
├── api/                       # API 封装
│   ├── request.ts
│   ├── auth.ts
│   └── ...
├── assets/                    # 静态资源
├── components/                # 可复用组件
│   └── ...
├── layouts/                   # 布局组件
│   ├── AuthLayout.vue
│   ├── MainLayout.vue
│   └── ...
├── router/                    # 路由配置
│   └── index.ts
├── stores/                    # Pinia 状态管理
│   ├── auth.ts
│   └── ...
├── styles/                    # 样式
│   ├── variables.scss
│   └── index.scss
├── types/                     # TypeScript 类型
│   └── api.ts
├── utils/                     # 工具函数
└── views/                     # 页面组件
    ├── auth/
    │   ├── Login.vue
    │   └── ...
    └── ...
```

### 开发规范

1. **命名规范**
   - 组件文件：PascalCase (如 `Login.vue`)
   - 组件名：PascalCase (如 `<LoginCard>`)
   - 变量/函数：camelCase (如 `userName`, `handleLogin`)
   - 常量：UPPER_SNAKE_CASE (如 `VITE_API_URL`)

2. **组件结构**
   ```vue
   <template>
     <!-- 模板 -->
   </template>
   
   <script setup lang="ts">
   // 导入
   import { ref } from 'vue'
   
   // Props
   const props = defineProps<{
     title: string
   }>()
   
   // Emits
   const emit = defineEmits<{
     (e: 'submit', value: string): void
   }>()
   
   // 状态
   const loading = ref(false)
   
   // 方法
   const handleSubmit = async () => {
     // ...
   }
   </script>
   
   <style lang="scss" scoped>
   // 样式
   </style>
   ```

3. **状态管理**
   - 局部状态用 `ref`/`reactive`
   - 全局状态用 Pinia Store
   - 避免滥用全局状态

4. **API 调用**
   - 统一在 `api/` 目录管理
   - 使用封装的 `http` 工具
   - 统一错误处理

### 添加新页面

1. **创建视图组件**
   ```bash
   # 在 views/ 目录创建页面组件
   ```

2. **配置路由**
   ```typescript
   // 在 router/index.ts 添加路由
   ```

3. **创建 Store (如需要)**
   ```bash
   # 在 stores/ 目录创建状态管理
   ```

4. **创建 API (如需要)**
   ```bash
   # 在 api/ 目录创建 API 调用
   ```

## 测试指南

### 后端测试

```java
@SpringBootTest
public class AuthServiceTest {
    
    @Autowired
    private AuthService authService;
    
    @Test
    @DisplayName("用户登录 - 成功场景")
    public void testLogin_Success() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        // When
        LoginResponse response = authService.login(request);
        
        // Then
        assertNotNull(response.getToken());
        assertNotNull(response.getUser());
    }
}
```

### 前端测试

```typescript
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import Login from '@/views/auth/Login.vue'

describe('Login', () => {
  it('renders properly', () => {
    const wrapper = mount(Login)
    expect(wrapper.text()).toContain('登录')
  })
  
  it('handles form validation', async () => {
    const wrapper = mount(Login)
    const submitButton = wrapper.find('button[type="submit"]')
    
    await submitButton.trigger('click')
    
    // 验证错误提示显示
    expect(wrapper.text()).toContain('请输入邮箱')
  })
})
```

## Git 工作流

### 分支管理

```bash
# 主分支
main - 生产环境代码
develop - 开发分支

# 功能分支
feat/feature-name - 新功能
fix/bug-name - 修复 bug
chore/task-name - 杂项任务
```

### 提交规范

```bash
# 格式
<type>(<scope>): <subject>

# type
feat: 新功能
fix: Bug 修复
docs: 文档更新
style: 代码格式
refactor: 重构
test: 测试
chore: 构建/工具

# 示例
feat(auth): 实现用户登录功能
fix(api): 修复登录接口参数验证
docs(readme): 更新快速开始指南
```

### 提交流程

```bash
# 1. 创建分支
git checkout -b feat/your-feature

# 2. 开发并提交
git add .
git commit -m "feat(feature): 实现功能"

# 3. 同步远程代码
git fetch origin
git rebase origin/develop

# 4. 推送分支
git push origin feat/your-feature

# 5. 创建 Merge Request
```

## 调试技巧

### 后端调试

1. **日志调试**
   ```yaml
   # application.yml
   logging:
     level:
       com.pptai: DEBUG
   ```

2. **远程调试**
   ```bash
   # 添加 JVM 参数
   -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
   ```

3. **使用 Postman/Swagger**
   - Swagger UI: http://localhost:8080/swagger-ui.html

### 前端调试

1. **浏览器开发工具**
   - Chrome DevTools
   - Vue DevTools 插件

2. **调试模式**
   ```typescript
   // 在代码中添加断点
   debugger
   
   // 使用 console
   console.log()
   console.error()
   console.table()
   ```

## 常见问题

### Q: 数据库连接失败

**A**: 
1. 检查 Docker 容器是否运行
2. 验证数据库配置
3. 确认端口未被占用

### Q: 前端无法连接后端

**A**:
1. 检查反向代理配置
2. 确认后端服务已启动
3. 查看 CORS 设置

### Q: 测试失败

**A**:
1. 检查测试数据
2. 确认环境配置
3. 查看详细错误日志

## 性能优化

### 后端优化

1. **数据库**
   - 添加索引
   - 使用连接池
   - 批量操作

2. **缓存**
   - Redis 缓存热点数据
   - Spring Cache

3. **异步**
   - 使用 @Async
   - 消息队列

### 前端优化

1. **打包**
   - 代码分割
   - Tree Shaking
   - 压缩资源

2. **加载**
   - 懒加载路由
   - 图片懒加载
   - 预加载

## 部署

### 本地运行

```bash
# 启动所有服务
cd backend
docker-compose up -d
mvn spring-boot:run

# 前端
cd frontend
npm run dev
```

### 生产部署

详见部署文档。

---

文档最后更新：2026-04-16
