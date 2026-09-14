package com.example.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.model.Notice;
import com.example.model.NoticeStatus;

import java.util.List;

@Dao
public interface NoticeDao {
    @Query("SELECT * FROM notices WHERE status != 'ARCHIVED' ORDER BY pinned DESC, createdAt DESC")
    LiveData<List<Notice>> getActiveNoticesLiveData();

    @Query("SELECT * FROM notices WHERE status != 'ARCHIVED' ORDER BY pinned DESC, createdAt DESC")
    List<Notice> getActiveNoticesDirect();

    @Query("SELECT * FROM notices ORDER BY createdAt DESC")
    LiveData<List<Notice>> getAllNoticesLiveData();

    @Query("SELECT * FROM notices ORDER BY createdAt DESC")
    List<Notice> getAllNoticesDirect();

    @Query("SELECT * FROM notices WHERE id = :noticeId LIMIT 1")
    Notice getNoticeById(String noticeId);

    @Query("SELECT * FROM notices WHERE id = :noticeId LIMIT 1")
    LiveData<Notice> getNoticeByIdLiveData(String noticeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotice(Notice notice);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotices(List<Notice> notices);

    @Update
    void updateNotice(Notice notice);

    @Query("UPDATE notices SET status = :status, updatedAt = :updatedAt WHERE id = :noticeId")
    void updateNoticeStatus(String noticeId, NoticeStatus status, long updatedAt);

    @Query("UPDATE notices SET pinned = :pinned WHERE id = :noticeId")
    void toggleNoticePin(String noticeId, boolean pinned);

    @Query("DELETE FROM notices WHERE id = :noticeId")
    void deleteNotice(String noticeId);
}
