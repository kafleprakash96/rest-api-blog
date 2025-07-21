-- src/main/resources/db/migration/V3__Add_indexes_and_optimizations.sql
-- Add full-text search index on posts
ALTER TABLE posts ADD FULLTEXT(title, content, excerpt);

-- Add composite indexes for common queries
CREATE INDEX idx_posts_status_published_date ON posts(status, published_date DESC);
CREATE INDEX idx_posts_author_status ON posts(author_id, status);
CREATE INDEX idx_posts_featured_status ON posts(is_featured, status);

-- Add index for view count queries
CREATE INDEX idx_posts_status_view_count ON posts(status, view_count DESC);

-- Optimize post_tags table
CREATE INDEX idx_post_tags_tag_post ON post_tags(tag_id, post_id);
