package com.example.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;

import java.util.Objects;

@Entity(
        tableName = "notice_reads",
        primaryKeys = {"noticeId", "userId"},
        indices = {@Index(value = {"userId"}), @Index(value = {"noticeId"})}
)
public class NoticeRead {
    @NonNull
    private String noticeId;
    @NonNull
    private String userId;
    private long readAt;

    public NoticeRead(@NonNull String noticeId, @NonNull String userId, long readAt) {
        this.noticeId = noticeId;
        this.userId = userId;
        this.readAt = readAt;
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

    public long getReadAt() {
        return readAt;
    }

    public void setReadAt(long readAt) {
        this.readAt = readAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoticeRead that = (NoticeRead) o;
        return readAt == that.readAt &&
                Objects.equals(noticeId, that.noticeId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noticeId, userId, readAt);
    }
}
