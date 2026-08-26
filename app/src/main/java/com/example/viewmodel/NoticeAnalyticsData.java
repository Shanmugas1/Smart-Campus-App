package com.example.viewmodel;

import com.example.model.Notice;

public class NoticeAnalyticsData {
    private final Notice notice;
    private final int targetedCount;
    private final int readCount;
    private final int unreadCount;
    private final float readRatePercent;

    public NoticeAnalyticsData(Notice notice, int targetedCount, int readCount, int unreadCount, float readRatePercent) {
        this.notice = notice;
        this.targetedCount = targetedCount;
        this.readCount = readCount;
        this.unreadCount = unreadCount;
        this.readRatePercent = readRatePercent;
    }

    public Notice getNotice() {
        return notice;
    }

    public int getTargetedCount() {
        return targetedCount;
    }

    public int getReadCount() {
        return readCount;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public float getReadRatePercent() {
        return readRatePercent;
    }
}
