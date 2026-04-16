package com.pptai.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 通用错误
    SUCCESS("00000", "操作成功"),
    INTERNAL_ERROR("50000", "系统内部错误"),
    INVALID_PARAMS("40000", "参数验证失败"),
    
    // 认证错误
    UNAUTHORIZED("40100", "用户未授权"),
    TOKEN_EXPIRED("40101", "登录令牌已过期"),
    INVALID_TOKEN("40102", "无效的令牌"),
    INVALID_CREDENTIALS("40103", "用户名或密码错误"),
    ACCOUNT_LOCKED("40104", "账号已被锁定"),
    EMAIL_NOT_VERIFIED("40105", "邮箱未验证"),
    DUPLICATE_EMAIL("40901", "邮箱已被注册"),
    
    // 资源错误
    NOT_FOUND("40400", "资源不存在"),
    USER_NOT_FOUND("40401", "用户不存在"),
    PROJECT_NOT_FOUND("40402", "项目不存在"),
    TEMPLATE_NOT_FOUND("40403", "模板不存在"),
    SLIDE_NOT_FOUND("40404", "幻灯片不存在"),
    
    // AI 服务错误
    AI_SERVICE_ERROR("51000", "AI 服务异常"),
    AI_GENERATION_TIMEOUT("51001", "AI 生成超时"),
    AI_CONTENT_FILTERED("51002", "AI 生成内容被过滤"),
    
    // 导出错误
    EXPORT_FAILED("52000", "导出失败"),
    EXPORT_TIMEOUT("52001", "导出超时"),
    FILE_TOO_LARGE("52002", "文件过大"),
    UNSUPPORTED_FORMAT("52003", "不支持的格式"),
    
    // 限制错误
    RATE_LIMIT_EXCEEDED("42900", "请求频率超限"),
    QUOTA_EXCEEDED("42901", "配额已用完"),
    
    // 文件错误
    FILE_UPLOAD_FAILED("53000", "文件上传失败"),
    FILE_NOT_FOUND("53001", "文件不存在"),
    INVALID_FILE_TYPE("53002", "无效的文件类型");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
