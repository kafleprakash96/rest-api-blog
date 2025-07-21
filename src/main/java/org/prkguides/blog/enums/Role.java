package org.prkguides.blog.enums;

public enum Role {
    USER("User"),
    ADMIN("Admin"),
    MODERATOR("Moderator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
