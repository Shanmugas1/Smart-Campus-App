package com.example.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;

import java.util.Objects;

@Entity(
        tableName = "bookmarks",
        primaryKeys = {"noticeId", "userId"},
        indices = {@Index(value = {"userId"}), @Index(value = {"noticeId"})}
)
public class Bookmark {
    @NonNull
    private String noticeId;
    @NonNull
    private String userId;
    private long createdAt;

    public Bookmark(@NonNull String noticeId, @NonNull String userId, long createdAt) {
        this.noticeId = noticeId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    @NonNull
    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(@NonNull String noticeId) {
        this.noticeId = noticeId;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bookmark bookmark = (Bookmark) o;
        return createdAt == bookmark.createdAt &&
                Objects.equals(noticeId, bookmark.noticeId) &&
                Objects.equals(userId, bookmark.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noticeId, userId, createdAt);
    }
}
