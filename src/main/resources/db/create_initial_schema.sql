-- src/main/resources/db/migration/V1__Create_initial_schema.sql
-- Create Users table
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       first_name VARCHAR(50),
                       last_name VARCHAR(50),
                       bio TEXT,
                       avatar_url VARCHAR(255),
                       website_url VARCHAR(255),
                       role ENUM('USER', 'ADMIN', 'MODERATOR') NOT NULL DEFAULT 'USER',
                       is_active BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       INDEX idx_username (username),
                       INDEX idx_email (email),
                       INDEX idx_role (role),
                       INDEX idx_active (is_active)
);

-- Create Tags table
CREATE TABLE tags (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(50) NOT NULL UNIQUE,
                      slug VARCHAR(60) NOT NULL UNIQUE,
                      description VARCHAR(200),
                      color VARCHAR(7), -- Hex color code
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                      INDEX idx_name (name),
                      INDEX idx_slug (slug)
);

-- Create Posts table
CREATE TABLE posts (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(200) NOT NULL,
                       slug VARCHAR(250) NOT NULL UNIQUE,
                       excerpt VARCHAR(500),
                       content LONGTEXT NOT NULL,
                       featured_image_url VARCHAR(255),
                       reading_time_minutes INT,
                       view_count BIGINT NOT NULL DEFAULT 0,
                       status ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED', 'SCHEDULED') NOT NULL DEFAULT 'DRAFT',
                       published_date TIMESTAMP NULL,
                       meta_description VARCHAR(160),
                       meta_keywords VARCHAR(255),
                       author_id BIGINT NOT NULL,
                       is_featured BOOLEAN NOT NULL DEFAULT FALSE,
                       allow_comments BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,

                       INDEX idx_title (title),
                       INDEX idx_slug (slug),
                       INDEX idx_status (status),
                       INDEX idx_published_date (published_date),
                       INDEX idx_author (author_id),
                       INDEX idx_featured (is_featured),
                       INDEX idx_view_count (view_count),
                       INDEX idx_created_at (created_at),

                       UNIQUE KEY uk_title (title)
);

-- Create Post-Tag relationship table
CREATE TABLE post_tags (
                           post_id BIGINT NOT NULL,
                           tag_id BIGINT NOT NULL,

                           PRIMARY KEY (post_id, tag_id),
                           FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                           FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,

                           INDEX idx_post_id (post_id),
                           INDEX idx_tag_id (tag_id)
);





