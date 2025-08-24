-- Blog Database Creation Script
-- This script creates all tables required for the blog application
-- Execute this script to set up the database schema

-- Drop database if exists and create new one
DROP DATABASE IF EXISTS blog_app;
CREATE DATABASE blog_app CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog_app;

-- Create users table
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       first_name VARCHAR(50),
                       last_name VARCHAR(50),
                       bio TEXT(1000),
                       avatar_url VARCHAR(500),
                       website_url VARCHAR(500),
                       role ENUM('USER', 'ADMIN', 'MODERATOR') NOT NULL DEFAULT 'USER',
                       is_active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       INDEX idx_user_username (username),
                       INDEX idx_user_email (email),
                       INDEX idx_user_role (role),
                       INDEX idx_user_active (is_active)
);

-- Create tags table
CREATE TABLE tags (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(50) NOT NULL UNIQUE,
                      slug VARCHAR(60) NOT NULL UNIQUE,
                      description VARCHAR(200),
                      color VARCHAR(7), -- Hex color code
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                      INDEX idx_tag_name (name),
                      INDEX idx_tag_slug (slug)
);

-- Create posts table
CREATE TABLE posts (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(200) NOT NULL,
                       slug VARCHAR(250) NOT NULL UNIQUE,
                       excerpt VARCHAR(500),
                       content LONGTEXT,
                       featured_image_url VARCHAR(500),
                       reading_time INT,
                       view_count BIGINT DEFAULT 0,
                       status ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED', 'SCHEDULED') NOT NULL DEFAULT 'DRAFT',
                       published_date TIMESTAMP NULL,
                       meta_description VARCHAR(160),
                       meta_keywords VARCHAR(255),
                       author_id BIGINT NOT NULL,
                       is_featured BOOLEAN DEFAULT FALSE,
                       allow_comments BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
                       UNIQUE KEY uk_post_title (title),
                       INDEX idx_post_status (status),
                       INDEX idx_post_published_date (published_date),
                       INDEX idx_post_author (author_id),
                       INDEX idx_post_slug (slug),
                       INDEX idx_post_featured (is_featured),
                       INDEX idx_post_view_count (view_count)
);

-- Create post_tags junction table
CREATE TABLE post_tags (
                           post_id BIGINT NOT NULL,
                           tag_id BIGINT NOT NULL,

                           PRIMARY KEY (post_id, tag_id),
                           FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                           FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
                           INDEX idx_post_tags_post (post_id),
                           INDEX idx_post_tags_tag (tag_id)
);

-- Create comments table
CREATE TABLE comments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          content TEXT(1000) NOT NULL,
                          status ENUM('PENDING', 'APPROVED', 'REJECTED', 'SPAM') NOT NULL DEFAULT 'PENDING',
                          post_id BIGINT NOT NULL,
                          author_id BIGINT NOT NULL,
                          parent_comment_id BIGINT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                          FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
                          FOREIGN KEY (parent_comment_id) REFERENCES comments(id) ON DELETE CASCADE,
                          INDEX idx_comment_post (post_id),
                          INDEX idx_comment_author (author_id),
                          INDEX idx_comment_status (status),
                          INDEX idx_comment_parent (parent_comment_id),
                          INDEX idx_comment_created (created_at)
);

-- Create notifications table
CREATE TABLE notifications (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               title VARCHAR(200) NOT NULL,
                               message VARCHAR(500) NOT NULL,
                               type ENUM('COMMENT', 'REPLY', 'POST_PUBLISHED', 'POST_LIKED', 'MENTION', 'FOLLOW', 'SYSTEM') NOT NULL,
                               is_read BOOLEAN DEFAULT FALSE,
                               related_entity_id BIGINT,
                               action_url VARCHAR(500),
                               user_id BIGINT NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                               INDEX idx_notification_user (user_id),
                               INDEX idx_notification_read (is_read),
                               INDEX idx_notification_type (type),
                               INDEX idx_notification_created (created_at)
);

-- Create notification_settings table
CREATE TABLE notification_settings (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       user_id BIGINT NOT NULL UNIQUE,
                                       email_notifications BOOLEAN DEFAULT TRUE,
                                       comment_notifications BOOLEAN DEFAULT TRUE,
                                       post_publish_notifications BOOLEAN DEFAULT TRUE,
                                       like_notifications BOOLEAN DEFAULT TRUE,
                                       mention_notifications BOOLEAN DEFAULT TRUE,
                                       newsletter_subscription BOOLEAN DEFAULT FALSE,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                       INDEX idx_notification_settings_user (user_id)
);

-- Create newsletter_subscribers table
CREATE TABLE newsletter_subscribers (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        email VARCHAR(100) NOT NULL UNIQUE,
                                        name VARCHAR(100),
                                        is_active BOOLEAN DEFAULT TRUE,
                                        unsubscribe_token VARCHAR(100) UNIQUE,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                        INDEX idx_subscriber_email (email),
                                        INDEX idx_subscriber_active (is_active),
                                        INDEX idx_subscriber_token (unsubscribe_token)
);

-- Create password_reset_tokens table
CREATE TABLE password_reset_tokens (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       token VARCHAR(100) NOT NULL UNIQUE,
                                       expiry_date TIMESTAMP NOT NULL,
                                       is_used BOOLEAN DEFAULT FALSE,
                                       user_id BIGINT NOT NULL,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                       INDEX idx_reset_token (token),
                                       INDEX idx_reset_expiry (expiry_date),
                                       INDEX idx_reset_user (user_id)
);

-- Create view for post statistics (optional, for performance)
CREATE VIEW post_stats AS
SELECT
    p.id,
    p.title,
    p.slug,
    p.view_count,
    COUNT(DISTINCT c.id) as comment_count,
    COUNT(DISTINCT CASE WHEN c.status = 'APPROVED' THEN c.id END) as approved_comment_count,
    p.status,
    p.published_date,
    u.username as author_username,
    u.first_name as author_first_name,
    u.last_name as author_last_name
FROM posts p
         LEFT JOIN users u ON p.author_id = u.id
         LEFT JOIN comments c ON p.id = c.post_id
GROUP BY p.id, p.title, p.slug, p.view_count, p.status, p.published_date,
         u.username, u.first_name, u.last_name;

-- Create view for tag statistics (optional, for performance)
CREATE VIEW tag_stats AS
SELECT
    t.id,
    t.name,
    t.slug,
    t.description,
    t.color,
    COUNT(DISTINCT pt.post_id) as post_count,
    COUNT(DISTINCT CASE WHEN p.status = 'PUBLISHED' THEN pt.post_id END) as published_post_count
FROM tags t
         LEFT JOIN post_tags pt ON t.id = pt.tag_id
         LEFT JOIN posts p ON pt.post_id = p.id
GROUP BY t.id, t.name, t.slug, t.description, t.color;

-- Create view for user statistics (optional, for performance)
CREATE VIEW user_stats AS
SELECT
    u.id,
    u.username,
    u.email,
    u.first_name,
    u.last_name,
    u.role,
    u.is_active,
    COUNT(DISTINCT p.id) as total_posts,
    COUNT(DISTINCT CASE WHEN p.status = 'PUBLISHED' THEN p.id END) as published_posts,
    COUNT(DISTINCT c.id) as total_comments,
    SUM(DISTINCT p.view_count) as total_views
FROM users u
         LEFT JOIN posts p ON u.id = p.author_id
         LEFT JOIN comments c ON u.id = c.author_id
GROUP BY u.id, u.username, u.email, u.first_name, u.last_name, u.role, u.is_active;

-- Add triggers for automatic slug generation and timestamps (MySQL 8.0+)
DELIMITER $$

-- Trigger to automatically update the updated_at timestamp
CREATE TRIGGER tr_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER tr_posts_updated_at
    BEFORE UPDATE ON posts
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER tr_tags_updated_at
    BEFORE UPDATE ON tags
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER tr_comments_updated_at
    BEFORE UPDATE ON comments
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER tr_notifications_updated_at
    BEFORE UPDATE ON notifications
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER tr_notification_settings_updated_at
    BEFORE UPDATE ON notification_settings
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER tr_newsletter_subscribers_updated_at
    BEFORE UPDATE ON newsletter_subscribers
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

CREATE TRIGGER tr_password_reset_tokens_updated_at
    BEFORE UPDATE ON password_reset_tokens
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

DELIMITER ;

-- Insert initial data (optional - you mentioned you'll use APIs)
-- Uncomment the following if you want some initial data

/*
-- Insert default admin user (password: admin123)
INSERT INTO users (username, email, password, first_name, last_name, role, is_active) VALUES
('admin', 'admin@yourblog.com', '$2a$10$dXJ3SW6G7P3EBZ7NNQ7qFOLdXi9x4ZGlYIGdEV3l8.yJ9FtqoqG9i', 'Admin', 'User', 'ADMIN', true);

-- Insert some sample tags
INSERT INTO tags (name, slug, description, color) VALUES
('Spring Boot', 'spring-boot', 'Java Spring Boot framework', '#6db33f'),
('React', 'react', 'React JavaScript library', '#61dafb'),
('Database', 'database', 'Database related content', '#336791'),
('Tutorial', 'tutorial', 'Step-by-step tutorials', '#f39c12'),
('Best Practices', 'best-practices', 'Programming best practices', '#e74c3c');

-- Create default notification settings for admin user
INSERT INTO notification_settings (user_id, email_notifications, comment_notifications, post_publish_notifications, like_notifications, mention_notifications, newsletter_subscription) VALUES
(1, true, true, true, true, true, false);
*/

-- Final verification queries (optional - for testing)
-- You can run these to verify the schema was created correctly

/*
SELECT 'Database created successfully' as status;

-- Show all tables
SHOW TABLES;

-- Show table structures
DESCRIBE users;
DESCRIBE posts;
DESCRIBE tags;
DESCRIBE post_tags;
DESCRIBE comments;
DESCRIBE notifications;
DESCRIBE notification_settings;
DESCRIBE newsletter_subscribers;
DESCRIBE password_reset_tokens;

-- Show indexes
SHOW INDEX FROM posts;
SHOW INDEX FROM users;
SHOW INDEX FROM comments;

-- Show views
SHOW FULL TABLES WHERE Table_type = 'VIEW';
*/
