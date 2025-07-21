-- src/main/resources/db/migration/V4__Add_audit_fields.sql
-- Add audit fields to track changes
ALTER TABLE posts
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD FOREIGN KEY (created_by) REFERENCES users(id),
    ADD FOREIGN KEY (updated_by) REFERENCES users(id);

ALTER TABLE tags
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD FOREIGN KEY (created_by) REFERENCES users(id),
    ADD FOREIGN KEY (updated_by) REFERENCES users(id);

-- Update existing records with admin user
-- UPDATE posts SET created_by = 1, updated_by = 1 WHERE created_by IS NULL;
-- UPDATE tags SET created_by = 1, updated_by = 1 WHERE created_by IS NULL;
