# DeepSeek AI 配置指南

## 获取 API Key

1. 访问 DeepSeek 官网：https://deepseek.com
2. 注册/登录账号
3. 进入控制台：https://platform.deepseek.com
4. 创建 API Key
5. 复制保存 API Key（只显示一次）

## 配置方式

### 方式 1：环境变量（推荐）

在项目根目录创建 `.env` 文件：

```bash
# DeepSeek AI 配置
AI_API_URL=https://api.deepseek.com/v1/chat/completions
AI_API_KEY=sk-your-api-key-here
AI_API_MODEL=deepseek-chat
```

### 方式 2：直接修改配置文件

编辑 `backend/src/main/resources/application.yml`：

```yaml
ai:
  api:
    url: https://api.deepseek.com/v1/chat/completions
    key: sk-your-api-key-here
    model: deepseek-chat
```

### 方式 3：启动时传入

```bash
cd backend
mvn spring-boot:run \
  -Dspring-boot.run.arguments="--ai.api.key=sk-your-api-key-here"
```

## 可用模型

| 模型 | 说明 | 适用场景 |
|------|------|----------|
| deepseek-chat | 通用对话模型 | PPT 内容生成、大纲生成 |
| deepseek-coder | 代码专用模型 | 代码生成、技术文档 |
| deepseek-v2.5 | 最新版本 | 复杂任务、高质量内容 |

## 资费说明

- **deepseek-chat**: ¥0.2 / 1K tokens (输入), ¥0.8 / 1K tokens (输出)
- **deepseek-coder**: ¥0.2 / 1K tokens (输入), ¥0.8 / 1K tokens (输出)
- **deepseek-v2.5**: ¥1.0 / 1K tokens (输入), ¥2.0 / 1K tokens (输出)

参考：https://platform.deepseek.com/api-docs/pricing/

## 使用限制

- **免费额度**: 新用户注册赠送 ¥2 体验金
- **速率限制**: 
  - QPS: 10 请求/秒
  - RPM: 1000 请求/分钟
- **并发限制**: 根据账户等级而定

## 测试连接

```bash
# 使用 curl 测试
curl https://api.deepseek.com/v1/chat/completions \
  -H "Authorization: Bearer sk-your-api-key-here" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "deepseek-chat",
    "messages": [
      {"role": "system", "content": "你是一个有用的助手"},
      {"role": "user", "content": "你好"}
    ]
  }'
```

## 常见问题

### Q: API 调用失败？

**A**: 检查以下几点：
1. API Key 是否正确
2. 网络连接是否正常
3. 余额是否充足
4. 是否超过速率限制

### Q: 生成内容质量不佳？

**A**: 尝试以下优化：
1. 使用更具体的 prompt
2. 调整 temperature 参数（0.5-0.8）
3. 增加 max_tokens 限制
4. 升级到更高级的模型

### Q: 如何查看使用量？

**A**: 
1. 登录 DeepSeek 平台
2. 进入"用量统计"页面
3. 查看 token 使用情况和费用

## 代码示例

### Spring Boot 配置

```java
@Service
public class DeepSeekService {
    
    @Value("${ai.api.key}")
    private String apiKey;
    
    public String generate(String prompt) {
        // 实现调用逻辑
    }
}
```

### Python 示例

```python
from openai import OpenAI

client = OpenAI(
    api_key="sk-your-api-key-here",
    base_url="https://api.deepseek.com"
)

response = client.chat.completions.create(
    model="deepseek-chat",
    messages=[
        {"role": "system", "content": "你是一个有用的助手"},
        {"role": "user", "content": "你好"}
    ]
)

print(response.choices[0].message.content)
```

## 最佳实践

1. **错误处理**: 添加重试机制和降级方案
2. **缓存**: 对相同请求使用缓存
3. **限流**: 控制请求频率，避免超限
4. **监控**: 记录 API 使用情况和费用
5. **优化**: 精简 prompt，减少 token 使用

## 相关文档

- [DeepSeek API 文档](https://platform.deepseek.com/api-docs/)
- [模型介绍](https://platform.deepseek.com/api-docs/models/)
- [使用指南](https://platform.deepseek.com/api-docs/quickstart/)

---

最后更新：2026-04-16
