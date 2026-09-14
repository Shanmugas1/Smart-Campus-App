package com.example.model;

public enum Role {
    SUPER_ADMIN("Super Admin"),
    ADMIN("Administrator"),
    DEPARTMENT_ADMIN("Department Admin"),
    FACULTY("Faculty Member"),
    STUDENT("Student");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
