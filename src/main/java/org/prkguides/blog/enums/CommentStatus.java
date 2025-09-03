package org.prkguides.blog.enums;

import lombok.Getter;

@Getter
public enum CommentStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    SPAM("Spam");

    private final String displayName;

    CommentStatus(String displayName) {
        this.displayName = displayName;
    }
}
