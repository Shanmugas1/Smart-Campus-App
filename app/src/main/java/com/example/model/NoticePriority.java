package com.example.model;

public enum NoticePriority {
    NORMAL("Normal", 1),
    IMPORTANT("Important", 2),
    URGENT("Urgent", 3);

    private final String displayName;
    private final int level;

    NoticePriority(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }
}
