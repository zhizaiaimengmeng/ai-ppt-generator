-- 创建数据库
CREATE DATABASE IF NOT EXISTS ppt_ai DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ppt_ai;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    avatar_url VARCHAR(500),
    email_verified BOOLEAN DEFAULT FALSE,
    verification_token VARCHAR(255),
    reset_password_token VARCHAR(255),
    reset_password_expires DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- PPT 项目表
CREATE TABLE IF NOT EXISTS ppt_projects (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(500) NOT NULL,
    template_id BIGINT,
    generation_mode VARCHAR(50),
    status VARCHAR(50) DEFAULT 'draft',
    generation_progress INT DEFAULT 0,
    generation_message VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (template_id) REFERENCES templates(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 幻灯片表
CREATE TABLE IF NOT EXISTS slides (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    slide_number INT NOT NULL,
    layout_type VARCHAR(50),
    title VARCHAR(500),
    content JSON,
    background_color VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_project_id (project_id),
    INDEX idx_slide_number (slide_number),
    FOREIGN KEY (project_id) REFERENCES ppt_projects(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 模板表
CREATE TABLE IF NOT EXISTS templates (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(100),
    description TEXT,
    preview_url VARCHAR(500),
    template_file_url VARCHAR(500),
    color_scheme JSON,
    font_scheme JSON,
    slide_layouts JSON,
    is_premium BOOLEAN DEFAULT FALSE,
    download_count INT DEFAULT 0,
    favorite_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_premium (is_premium)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 导出记录表
CREATE TABLE IF NOT EXISTS export_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    export_format VARCHAR(50) NOT NULL,
    file_url VARCHAR(500),
    file_size BIGINT,
    status VARCHAR(50) DEFAULT 'pending',
    progress INT DEFAULT 0,
    error_message VARCHAR(500),
    share_url VARCHAR(500),
    share_password_hash VARCHAR(255),
    share_expires_at DATETIME,
    download_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_project_id (project_id),
    INDEX idx_share_url (share_url),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES ppt_projects(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户收藏表
CREATE TABLE IF NOT EXISTS user_favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    template_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_template (user_id, template_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (template_id) REFERENCES templates(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入初始模板数据
INSERT INTO templates (name, category, description, preview_url, is_premium, download_count, favorite_count) VALUES
('商务简约', '商务', '简洁专业的商务风格模板', '/templates/previews/business-simple.png', FALSE, 0, 0),
('科技蓝', '科技', '现代科技感蓝色调模板', '/templates/previews/tech-blue.png', FALSE, 0, 0),
('教育教学', '教育', '适合教学课件的清新模板', '/templates/previews/education.png', FALSE, 0, 0),
('医疗专业', '医疗', '医疗行业专业模板', '/templates/previews/medical.png', FALSE, 0, 0),
('创意艺术', '艺术', '充满创意的艺术设计模板', '/templates/previews/creative-art.png', FALSE, 0, 0),
('金融财经', '金融', '金融行业专业模板', '/templates/previews/finance.png', TRUE, 0, 0),
('极简风格', '通用', '极简主义风格通用模板', '/templates/previews/minimalist.png', FALSE, 0, 0),
('活力橙色', '通用', '充满活力的橙色主题模板', '/templates/previews/energetic-orange.png', FALSE, 0, 0),
('优雅紫色', '通用', '优雅紫色调模板', '/templates/previews/elegant-purple.png', FALSE, 0, 0),
('自然绿色', '通用', '清新自然绿色主题模板', '/templates/previews/nature-green.png', FALSE, 0, 0);
