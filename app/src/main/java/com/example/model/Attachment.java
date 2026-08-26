package com.example.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(
        tableName = "attachments",
        indices = {
                @Index(value = {"noticeId"}),
                @Index(value = {"storageKey"}, unique = true),
                @Index(value = {"uploadedBy"}),
                @Index(value = {"createdAt"})
        }
)
public class Attachment {
    @PrimaryKey
    @NonNull
    private String id;
    @NonNull
    private String noticeId;
    @NonNull
    private String originalFileName;
    @NonNull
    private String storedFileName;
    @NonNull
    private String contentType;
    @NonNull
    private String fileExtension;
    private long fileSize;
    @NonNull
    private String fileSizeFormatted;
    @NonNull
    private String storageKey;
    @NonNull
    private String uploadedBy;
    private long createdAt;
    private int downloadCount;

    @androidx.room.Ignore
    public Attachment(@NonNull String id,
                      @NonNull String noticeId,
                      @NonNull String originalFileName,
                      @NonNull String storedFileName,
                      @NonNull String contentType,
                      @NonNull String fileExtension,
                      long fileSize,
                      @NonNull String fileSizeFormatted,
                      @NonNull String storageKey,
                      @NonNull String uploadedBy,
                      long createdAt) {
        this(id, noticeId, originalFileName, storedFileName, contentType, fileExtension, fileSize, fileSizeFormatted, storageKey, uploadedBy, createdAt, 0);
    }

    public Attachment(@NonNull String id,
                      @NonNull String noticeId,
                      @NonNull String originalFileName,
                      @NonNull String storedFileName,
                      @NonNull String contentType,
                      @NonNull String fileExtension,
                      long fileSize,
                      @NonNull String fileSizeFormatted,
                      @NonNull String storageKey,
                      @NonNull String uploadedBy,
                      long createdAt,
                      int downloadCount) {
        this.id = id;
        this.noticeId = noticeId;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.contentType = contentType;
        this.fileExtension = fileExtension;
        this.fileSize = fileSize;
        this.fileSizeFormatted = fileSizeFormatted;
        this.storageKey = storageKey;
        this.uploadedBy = uploadedBy;
        this.createdAt = createdAt;
        this.downloadCount = downloadCount;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(@NonNull String noticeId) {
        this.noticeId = noticeId;
    }

    @NonNull
    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(@NonNull String originalFileName) {
        this.originalFileName = originalFileName;
    }

    @NonNull
    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(@NonNull String storedFileName) {
        this.storedFileName = storedFileName;
    }

    @NonNull
    public String getContentType() {
        return contentType;
    }

    public void setContentType(@NonNull String contentType) {
        this.contentType = contentType;
    }

    @NonNull
    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(@NonNull String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    @NonNull
    public String getFileSizeFormatted() {
        return fileSizeFormatted;
    }

    public void setFileSizeFormatted(@NonNull String fileSizeFormatted) {
        this.fileSizeFormatted = fileSizeFormatted;
    }

    @NonNull
    public String getStorageKey() {
        return storageKey;
    }

    public void setStorageKey(@NonNull String storageKey) {
        this.storageKey = storageKey;
    }

    @NonNull
    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(@NonNull String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attachment that = (Attachment) o;
        return fileSize == that.fileSize &&
                createdAt == that.createdAt &&
                downloadCount == that.downloadCount &&
                Objects.equals(id, that.id) &&
                Objects.equals(noticeId, that.noticeId) &&
                Objects.equals(originalFileName, that.originalFileName) &&
                Objects.equals(storedFileName, that.storedFileName) &&
                Objects.equals(contentType, that.contentType) &&
                Objects.equals(fileExtension, that.fileExtension) &&
                Objects.equals(fileSizeFormatted, that.fileSizeFormatted) &&
                Objects.equals(storageKey, that.storageKey) &&
                Objects.equals(uploadedBy, that.uploadedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, noticeId, originalFileName, storedFileName, contentType, fileExtension, fileSize, fileSizeFormatted, storageKey, uploadedBy, createdAt, downloadCount);
    }
}
