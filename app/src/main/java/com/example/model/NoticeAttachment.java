package com.example.model;

import java.util.Objects;

public class NoticeAttachment {
    private final String id;
    private final String fileName;
    private final String fileType; // "PDF", "IMAGE", "DOC"
    private final String fileSize; // e.g. "1.4 MB"
    private final String fileUrl;

    public NoticeAttachment(String id, String fileName, String fileType, String fileSize, String fileUrl) {
        this.id = id != null ? id : "";
        this.fileName = fileName != null ? fileName : "";
        this.fileType = fileType != null ? fileType : "DOC";
        this.fileSize = fileSize != null ? fileSize : "0 KB";
        this.fileUrl = fileUrl != null ? fileUrl : "";
    }

    public NoticeAttachment(String id, String fileName, String fileType, String fileSize) {
        this(id, fileName, fileType, fileSize, "");
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoticeAttachment that = (NoticeAttachment) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(fileType, that.fileType) &&
                Objects.equals(fileSize, that.fileSize) &&
                Objects.equals(fileUrl, that.fileUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName, fileType, fileSize, fileUrl);
    }

    @Override
    public String toString() {
        return "NoticeAttachment{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                '}';
    }
}
