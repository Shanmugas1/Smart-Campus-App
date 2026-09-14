package com.example.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.model.NoticeRead;

import java.util.List;

@Dao
public interface NoticeReadDao {
    @Query("SELECT * FROM notice_reads WHERE userId = :userId")
    LiveData<List<NoticeRead>> getReadsForUserLiveData(String userId);

    @Query("SELECT * FROM notice_reads WHERE userId = :userId")
    List<NoticeRead> getReadsForUserDirect(String userId);

    @Query("SELECT COUNT(*) FROM notice_reads WHERE noticeId = :noticeId")
    LiveData<Integer> getReadCountForNoticeLiveData(String noticeId);

    @Query("SELECT COUNT(*) FROM notice_reads WHERE noticeId = :noticeId")
    int getReadCountForNoticeDirect(String noticeId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void markNoticeAsRead(NoticeRead noticeRead);

    @Query("DELETE FROM notice_reads WHERE noticeId = :noticeId AND userId = :userId")
    void unmarkNoticeAsRead(String noticeId, String userId);
}
