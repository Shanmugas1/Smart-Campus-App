package com.example.model;

public enum AttachmentCategoryType {
    PDF("PDF"),
    WORD("DOCX"),
    POWERPOINT("PPTX"),
    EXCEL("XLSX"),
    IMAGE("IMAGE"),
    DOCUMENT("DOC");

    private final String badge;

    AttachmentCategoryType(String badge) {
        this.badge = badge;
    }

    public String getBadge() {
        return badge;
    }
}
