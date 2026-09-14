package com.example.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class NoticeWithState {
    @NonNull
    private final Notice notice;
    private final boolean isRead;
    private final boolean isBookmarked;

    public NoticeWithState(@NonNull Notice notice, boolean isRead, boolean isBookmarked) {
        this.notice = notice;
        this.isRead = isRead;
        this.isBookmarked = isBookmarked;
    }

    @NonNull
    public Notice getNotice() {
        return notice;
    }

    public boolean isRead() {
        return isRead;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoticeWithState that = (NoticeWithState) o;
        return isRead == that.isRead &&
                isBookmarked == that.isBookmarked &&
                Objects.equals(notice, that.notice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notice, isRead, isBookmarked);
    }

    @Override
    public String toString() {
        return "NoticeWithState{" +
                "notice=" + notice +
                ", isRead=" + isRead +
                ", isBookmarked=" + isBookmarked +
                '}';
    }
}
