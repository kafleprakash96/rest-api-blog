package org.prkguides.blog.enums;

import lombok.Getter;

@Getter
public enum PostStatus {
    DRAFT("Draft"),
    PUBLISHED("Published"),
    ARCHIVED("Archived"),
    SCHEDULED("Scheduled");

    private final String displayName;

    PostStatus(String displayName) {
        this.displayName = displayName;
    }

}
