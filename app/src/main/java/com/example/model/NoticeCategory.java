package com.example.model;

public enum NoticeCategory {
    ACADEMIC("Academic", "School"),
    EXAMINATION("Examination", "Assignment"),
    PLACEMENT("Placement", "Work"),
    EVENT("Event", "Event"),
    EMERGENCY("Emergency", "Warning"),
    GENERAL("General", "Campaign");

    private final String displayName;
    private final String iconName;

    NoticeCategory(String displayName, String iconName) {
        this.displayName = displayName;
        this.iconName = iconName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIconName() {
        return iconName;
    }
}
