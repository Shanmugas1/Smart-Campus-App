package com.example.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.model.Attachment;

import java.util.List;

@Dao
public interface AttachmentDao {
    @Query("SELECT * FROM attachments WHERE noticeId = :noticeId ORDER BY createdAt ASC")
    LiveData<List<Attachment>> getAttachmentsForNoticeLiveData(String noticeId);

    @Query("SELECT * FROM attachments WHERE noticeId = :noticeId ORDER BY createdAt ASC")
    List<Attachment> getAttachmentsForNoticeDirect(String noticeId);

    @Query("SELECT * FROM attachments ORDER BY createdAt DESC")
    LiveData<List<Attachment>> getAllAttachmentsLiveData();

    @Query("SELECT * FROM attachments ORDER BY createdAt DESC")
    List<Attachment> getAllAttachmentsDirect();

    @Query("SELECT * FROM attachments WHERE id = :attachmentId LIMIT 1")
    Attachment getAttachmentById(String attachmentId);

    @Query("SELECT * FROM attachments WHERE storageKey = :storageKey LIMIT 1")
    Attachment getAttachmentByStorageKey(String storageKey);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAttachment(Attachment attachment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAttachments(List<Attachment> attachments);

    @Query("DELETE FROM attachments WHERE id = :attachmentId")
    void deleteAttachment(String attachmentId);

    @Query("DELETE FROM attachments WHERE noticeId = :noticeId")
    void deleteAttachmentsForNotice(String noticeId);

    @Query("UPDATE attachments SET downloadCount = downloadCount + 1 WHERE id = :attachmentId")
    void incrementDownloadCount(String attachmentId);
}
