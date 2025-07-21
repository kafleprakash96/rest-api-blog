package org.prkguides.blog.enums;

public enum PostStatus {
    DRAFT("Draft"),
    PUBLISHED("Published"),
    ARCHIVED("Archived"),
    SCHEDULED("Scheduled");

    private final String displayName;

    PostStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
