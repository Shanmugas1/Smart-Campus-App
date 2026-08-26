package com.example.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(
        tableName = "notices",
        indices = {
                @Index(value = {"status"}),
                @Index(value = {"category"}),
                @Index(value = {"priority"}),
                @Index(value = {"createdAt"})
        }
)
public class Notice {
    @PrimaryKey
    @NonNull
    private String id;
    @NonNull
    private String title;
    @NonNull
    private String content;
    @NonNull
    private NoticeCategory category;
    @NonNull
    private NoticePriority priority;
    @NonNull
    private String postedBy;
    @NonNull
    private String postedById;
    @NonNull
    private String targetAudience;
    @NonNull
    private NoticeStatus status;
    private boolean pinned;
    private boolean approved;
    private long createdAt;
    private long updatedAt;
    @Nullable
    private Long scheduledAt;
    @Nullable
    private Long expiresAt;
    @NonNull
    private String attachmentsJson;
    private boolean isOfficial;

    public Notice(@NonNull String id,
                  @NonNull String title,
                  @NonNull String content,
                  @NonNull NoticeCategory category,
                  @NonNull NoticePriority priority,
                  @NonNull String postedBy,
                  @NonNull String postedById,
                  @NonNull String targetAudience,
                  @NonNull NoticeStatus status,
                  boolean pinned,
                  boolean approved,
                  long createdAt,
                  long updatedAt,
                  @Nullable Long scheduledAt,
                  @Nullable Long expiresAt,
                  @NonNull String attachmentsJson,
                  boolean isOfficial) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.priority = priority;
        this.postedBy = postedBy;
        this.postedById = postedById;
        this.targetAudience = targetAudience;
        this.status = status;
        this.pinned = pinned;
        this.approved = approved;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.scheduledAt = scheduledAt;
        this.expiresAt = expiresAt;
        this.attachmentsJson = attachmentsJson;
        this.isOfficial = isOfficial;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    @NonNull
    public NoticeCategory getCategory() {
        return category;
    }

    public void setCategory(@NonNull NoticeCategory category) {
        this.category = category;
    }

    @NonNull
    public NoticePriority getPriority() {
        return priority;
    }

    public void setPriority(@NonNull NoticePriority priority) {
        this.priority = priority;
    }

    @NonNull
    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(@NonNull String postedBy) {
        this.postedBy = postedBy;
    }

    @NonNull
    public String getPostedById() {
        return postedById;
    }

    public void setPostedById(@NonNull String postedById) {
        this.postedById = postedById;
    }

    @NonNull
    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(@NonNull String targetAudience) {
        this.targetAudience = targetAudience;
    }

    @NonNull
    public NoticeStatus getStatus() {
        return status;
    }

    public void setStatus(@NonNull NoticeStatus status) {
        this.status = status;
    }

    public boolean isPinned() {
        return pinned;
    }

    @androidx.room.Ignore
    public boolean getPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Nullable
    public Long getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(@Nullable Long scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    @Nullable
    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(@Nullable Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    @NonNull
    public String getAttachmentsJson() {
        return attachmentsJson;
    }

    public void setAttachmentsJson(@NonNull String attachmentsJson) {
        this.attachmentsJson = attachmentsJson;
    }

    public boolean isOfficial() {
        return isOfficial;
    }

    public void setOfficial(boolean official) {
        isOfficial = official;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notice notice = (Notice) o;
        return pinned == notice.pinned &&
                approved == notice.approved &&
                createdAt == notice.createdAt &&
                updatedAt == notice.updatedAt &&
                isOfficial == notice.isOfficial &&
                Objects.equals(id, notice.id) &&
                Objects.equals(title, notice.title) &&
                Objects.equals(content, notice.content) &&
                category == notice.category &&
                priority == notice.priority &&
                Objects.equals(postedBy, notice.postedBy) &&
                Objects.equals(postedById, notice.postedById) &&
                Objects.equals(targetAudience, notice.targetAudience) &&
                status == notice.status &&
                Objects.equals(scheduledAt, notice.scheduledAt) &&
                Objects.equals(expiresAt, notice.expiresAt) &&
                Objects.equals(attachmentsJson, notice.attachmentsJson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, category, priority, postedBy, postedById, targetAudience, status, pinned, approved, createdAt, updatedAt, scheduledAt, expiresAt, attachmentsJson, isOfficial);
    }
}
