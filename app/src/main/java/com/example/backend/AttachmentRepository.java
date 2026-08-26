package com.example.backend;

import androidx.lifecycle.LiveData;

import com.example.data.AttachmentDao;
import com.example.model.Attachment;

import java.util.List;

/**
 * Spring Boot Style Attachment Data Access Repository
 */
public class AttachmentRepository {

    private final AttachmentDao attachmentDao;

    public AttachmentRepository(AttachmentDao attachmentDao) {
        this.attachmentDao = attachmentDao;
    }

    public LiveData<List<Attachment>> findByNoticeIdLiveData(String noticeId) {
        return attachmentDao.getAttachmentsForNoticeLiveData(noticeId);
    }

    public List<Attachment> findByNoticeIdDirect(String noticeId) {
        return attachmentDao.getAttachmentsForNoticeDirect(noticeId);
    }

    public Attachment findById(String id) {
        return attachmentDao.getAttachmentById(id);
    }

    public Attachment findByStorageKey(String storageKey) {
        return attachmentDao.getAttachmentByStorageKey(storageKey);
    }

    public LiveData<List<Attachment>> findAllLiveData() {
        return attachmentDao.getAllAttachmentsLiveData();
    }

    public List<Attachment> findAllDirect() {
        return attachmentDao.getAllAttachmentsDirect();
    }

    public void save(Attachment attachment) {
        attachmentDao.insertAttachment(attachment);
    }

    public void saveAll(List<Attachment> attachments) {
        attachmentDao.insertAttachments(attachments);
    }

    public void deleteById(String id) {
        attachmentDao.deleteAttachment(id);
    }

    public void deleteByNoticeId(String noticeId) {
        attachmentDao.deleteAttachmentsForNotice(noticeId);
    }

    public void incrementDownloadCount(String id) {
        attachmentDao.incrementDownloadCount(id);
    }
}
