-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    display_name VARCHAR(50),
    email VARCHAR(100),
    avatar_url TEXT,
    remember_me_token VARCHAR(255),
    password_changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 初始化默认用户数据
INSERT INTO users (username, password, role, display_name) 
VALUES ('admin', 'admin123', 'ADMIN', '管理员'),
       ('user', 'user123', 'USER', '普通用户')
ON DUPLICATE KEY UPDATE username=username;

-- 创建通知表
CREATE TABLE IF NOT EXISTS notifications (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
title VARCHAR(255) NOT NULL,
content TEXT NOT NULL,
status VARCHAR(20) NOT NULL DEFAULT 'active',
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
created_by BIGINT NOT NULL,
FOREIGN KEY (created_by) REFERENCES users(id)
);

-- 更新没有display_name的用户，默认使用username作为display_name
UPDATE users SET display_name = username WHERE display_name IS NULL;

