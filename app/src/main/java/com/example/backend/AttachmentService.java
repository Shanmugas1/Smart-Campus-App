package com.example.backend;

import androidx.lifecycle.LiveData;

import com.example.data.NoticeDao;
import com.example.model.Attachment;
import com.example.model.Notice;
import com.example.model.Role;
import com.example.model.User;
import com.example.service.AudienceEngine;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Spring Boot Style Business Service for Institutional Attachments.
 * Enforces rigorous Role-Based Access Control (RBAC) and Hierarchical Audience Authorization.
 */
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;
    private final NoticeDao noticeDao;

    public AttachmentService(
            AttachmentRepository attachmentRepository,
            FileStorageService fileStorageService,
            NoticeDao noticeDao
    ) {
        this.attachmentRepository = attachmentRepository;
        this.fileStorageService = fileStorageService;
        this.noticeDao = noticeDao;
    }

    public static class AttachmentPreviewResult {
        private final Attachment attachment;
        private final String signedUrl;
        private final byte[] fileBytes;

        public AttachmentPreviewResult(Attachment attachment, String signedUrl, byte[] fileBytes) {
            this.attachment = attachment;
            this.signedUrl = signedUrl;
            this.fileBytes = fileBytes;
        }

        public Attachment getAttachment() {
            return attachment;
        }

        public String getSignedUrl() {
            return signedUrl;
        }

        public byte[] getFileBytes() {
            return fileBytes;
        }
    }

    /**
     * Upload an official announcement attachment.
     * Accessible ONLY to authorized Administrators and Faculty members.
     */
    public Attachment uploadAttachment(
            User user,
            String noticeId,
            String fileName,
            String contentType,
            byte[] fileBytes
    ) {
        if (user == null) {
            throw new SecurityException("401 UNAUTHORIZED: Authentication is required to upload attachments.");
        }
        if (user.getRole() == Role.STUDENT) {
            throw new SecurityException("403 FORBIDDEN: Students are not permitted to upload official announcement attachments.");
        }

        FileStorageService.StoredFileResult storeResult = fileStorageService.storeFile(
                fileName,
                contentType,
                fileBytes,
                noticeId
        );

        String id = "att_" + UUID.randomUUID().toString().substring(0, 8);
        Attachment attachment = new Attachment(
                id,
                noticeId,
                storeResult.getOriginalFileName(),
                storeResult.getStoredFileName(),
                storeResult.getContentType(),
                storeResult.getFileExtension(),
                storeResult.getFileSize(),
                storeResult.getFileSizeFormatted(),
                storeResult.getStorageKey(),
                user.getId(),
                System.currentTimeMillis(),
                0
        );

        attachmentRepository.save(attachment);
        return attachment;
    }

    /**
     * Downloads an attachment securely.
     * Authenticates user and verifies that the student is part of the announcement's target audience.
     */
    public byte[] getAttachmentForDownload(User user, String attachmentId) {
        if (user == null) {
            throw new SecurityException("401 UNAUTHORIZED: Institutional authentication is required to access announcement documents.");
        }

        Attachment attachment = attachmentRepository.findById(attachmentId);
        if (attachment == null) {
            throw new NoSuchElementException("Attachment not found for id: " + attachmentId);
        }

        Notice notice = noticeDao.getNoticeById(attachment.getNoticeId());
        if (notice == null) {
            throw new NoSuchElementException("Associated announcement not found for notice id: " + attachment.getNoticeId());
        }

        boolean isAuthorized = AudienceEngine.isStudentAuthorized(user, notice.getTargetAudience());
        if (!isAuthorized) {
            throw new SecurityException("403 FORBIDDEN: User '" + user.getName() + "' (" + user.getDepartment() + " " + user.getYear() + " " + user.getSection() + ") is not authorized to access attachments for this notice audience.");
        }

        attachmentRepository.incrementDownloadCount(attachment.getId());
        return fileStorageService.retrieveFile(attachment.getStorageKey());
    }

    /**
     * Generates a preview result with safe signed URL and byte payload for in-app viewing (e.g. PDF viewer).
     */
    public AttachmentPreviewResult getAttachmentPreview(User user, String attachmentId) {
        if (user == null) {
            throw new SecurityException("401 UNAUTHORIZED: Authentication required.");
        }

        Attachment attachment = attachmentRepository.findById(attachmentId);
        if (attachment == null) {
            throw new NoSuchElementException("Attachment not found for id: " + attachmentId);
        }

        Notice notice = noticeDao.getNoticeById(attachment.getNoticeId());
        if (notice == null) {
            throw new NoSuchElementException("Associated announcement not found");
        }

        boolean isAuthorized = AudienceEngine.isStudentAuthorized(user, notice.getTargetAudience());
        if (!isAuthorized) {
            throw new SecurityException("403 FORBIDDEN: User is not authorized to preview documents belonging to cohort '" + notice.getTargetAudience() + "'.");
        }

        String signedUrl = fileStorageService.generateSignedUrl(attachment.getStorageKey(), user.getId());
        byte[] fileBytes = fileStorageService.retrieveFile(attachment.getStorageKey());

        return new AttachmentPreviewResult(attachment, signedUrl, fileBytes);
    }

    public List<Attachment> getAttachmentsForNotice(User user, String noticeId) {
        if (user == null) {
            return Collections.emptyList();
        }

        Notice notice = noticeDao.getNoticeById(noticeId);
        if (notice == null) {
            return Collections.emptyList();
        }
        boolean isAuthorized = AudienceEngine.isStudentAuthorized(user, notice.getTargetAudience());
        if (!isAuthorized) {
            return Collections.emptyList();
        }

        return attachmentRepository.findByNoticeIdDirect(noticeId);
    }

    public LiveData<List<Attachment>> getAttachmentsForNoticeLiveData(String noticeId) {
        return attachmentRepository.findByNoticeIdLiveData(noticeId);
    }

    public LiveData<List<Attachment>> getAllAttachmentsLiveData() {
        return attachmentRepository.findAllLiveData();
    }

    public List<Attachment> getAllAttachmentsDirect() {
        return attachmentRepository.findAllDirect();
    }

    public void deleteAttachment(User user, String attachmentId) {
        if (user == null) {
            throw new SecurityException("401 UNAUTHORIZED: Authentication required.");
        }
        Attachment attachment = attachmentRepository.findById(attachmentId);
        if (attachment == null) {
            return;
        }

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.SUPER_ADMIN && !attachment.getUploadedBy().equals(user.getId())) {
            throw new SecurityException("403 FORBIDDEN: Only authorized administrators or the original publisher can delete attachments.");
        }

        fileStorageService.deleteFile(attachment.getStorageKey());
        attachmentRepository.deleteById(attachmentId);
    }

    public void cleanupNoticeAttachments(String noticeId) {
        fileStorageService.deleteFilesForNotice(noticeId);
        attachmentRepository.deleteByNoticeId(noticeId);
    }
}
