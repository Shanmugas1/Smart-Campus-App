package com.example.data;

import androidx.lifecycle.LiveData;

import com.example.backend.AttachmentController;
import com.example.backend.AttachmentRepository;
import com.example.backend.AttachmentService;
import com.example.backend.FileStorageService;
import com.example.model.Attachment;
import com.example.model.AuditLog;
import com.example.model.Bookmark;
import com.example.model.Department;
import com.example.model.Notice;
import com.example.model.NoticeCategory;
import com.example.model.NoticePriority;
import com.example.model.NoticeRead;
import com.example.model.NoticeStatus;
import com.example.model.NoticeWithState;
import com.example.model.Role;
import com.example.model.User;
import com.example.service.AudienceEngine;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CampusRepository {

    private final SmartCampusDatabase database;
    private final UserDao userDao;
    private final NoticeDao noticeDao;
    private final NoticeReadDao noticeReadDao;
    private final BookmarkDao bookmarkDao;
    private final DepartmentDao departmentDao;
    private final AuditLogDao auditLogDao;
    private final AttachmentDao attachmentDao;

    private final FileStorageService fileStorageService;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentService attachmentService;
    private final AttachmentController attachmentController;

    public CampusRepository(SmartCampusDatabase database) {
        this.database = database;
        this.userDao = database.userDao();
        this.noticeDao = database.noticeDao();
        this.noticeReadDao = database.noticeReadDao();
        this.bookmarkDao = database.bookmarkDao();
        this.departmentDao = database.departmentDao();
        this.auditLogDao = database.auditLogDao();
        this.attachmentDao = database.attachmentDao();

        this.fileStorageService = new FileStorageService();
        this.attachmentRepository = new AttachmentRepository(attachmentDao);
        this.attachmentService = new AttachmentService(attachmentRepository, fileStorageService, noticeDao);
        this.attachmentController = new AttachmentController(attachmentService);
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public NoticeDao getNoticeDao() {
        return noticeDao;
    }

    public NoticeReadDao getNoticeReadDao() {
        return noticeReadDao;
    }

    public BookmarkDao getBookmarkDao() {
        return bookmarkDao;
    }

    public DepartmentDao getDepartmentDao() {
        return departmentDao;
    }

    public AuditLogDao getAuditLogDao() {
        return auditLogDao;
    }

    public AttachmentDao getAttachmentDao() {
        return attachmentDao;
    }

    public FileStorageService getFileStorageService() {
        return fileStorageService;
    }

    public AttachmentRepository getAttachmentRepository() {
        return attachmentRepository;
    }

    public AttachmentService getAttachmentService() {
        return attachmentService;
    }

    public AttachmentController getAttachmentController() {
        return attachmentController;
    }

    public LiveData<List<User>> getAllUsersLiveData() {
        return userDao.getAllUsersLiveData();
    }

    public LiveData<List<Department>> getAllDepartmentsLiveData() {
        return departmentDao.getAllDepartmentsLiveData();
    }

    public LiveData<List<AuditLog>> getRecentAuditLogsLiveData() {
        return auditLogDao.getRecentAuditLogsLiveData();
    }

    public LiveData<Integer> getActiveStudentCountLiveData() {
        return userDao.getActiveStudentCountLiveData();
    }

    public LiveData<List<Attachment>> getAllAttachmentsLiveData() {
        return attachmentRepository.findAllLiveData();
    }

    public List<NoticeWithState> getPersonalizedNoticesDirect(User currentUser) {
        if (currentUser == null) {
            return Collections.emptyList();
        }
        List<Notice> notices = noticeDao.getActiveNoticesDirect();
        List<NoticeRead> reads = noticeReadDao.getReadsForUserDirect(currentUser.getId());
        List<Bookmark> bookmarks = bookmarkDao.getBookmarksForUserDirect(currentUser.getId());

        Set<String> readIds = new HashSet<>();
        for (NoticeRead r : reads) {
            readIds.add(r.getNoticeId());
        }

        Set<String> bookmarkIds = new HashSet<>();
        for (Bookmark b : bookmarks) {
            bookmarkIds.add(b.getNoticeId());
        }

        List<NoticeWithState> result = new ArrayList<>();
        for (Notice notice : notices) {
            if (AudienceEngine.isStudentAuthorized(currentUser, notice.getTargetAudience())) {
                result.add(new NoticeWithState(
                        notice,
                        readIds.contains(notice.getId()),
                        bookmarkIds.contains(notice.getId())
                ));
            }
        }
        return result;
    }

    public List<Notice> getAllNoticesForAdminDirect() {
        return noticeDao.getAllNoticesDirect();
    }

    public Notice getNoticeById(String noticeId) {
        return noticeDao.getNoticeById(noticeId);
    }

    public LiveData<Notice> getNoticeByIdLiveData(String noticeId) {
        return noticeDao.getNoticeByIdLiveData(noticeId);
    }

    public LiveData<List<Attachment>> getAttachmentsForNoticeLiveData(String noticeId) {
        return attachmentService.getAttachmentsForNoticeLiveData(noticeId);
    }

    public List<Attachment> getAttachmentsForNotice(User user, String noticeId) {
        return attachmentService.getAttachmentsForNotice(user, noticeId);
    }

    public boolean isNoticeBookmarked(String noticeId, String userId) {
        return bookmarkDao.isBookmarkedDirect(noticeId, userId);
    }

    public int getNoticeReadCount(String noticeId) {
        return noticeReadDao.getReadCountForNoticeDirect(noticeId);
    }

    public void markNoticeAsRead(String noticeId, String userId) {
        noticeReadDao.markNoticeAsRead(new NoticeRead(noticeId, userId, System.currentTimeMillis()));
    }

    public void toggleBookmark(String noticeId, String userId, boolean isCurrentlyBookmarked) {
        if (isCurrentlyBookmarked) {
            bookmarkDao.removeBookmark(noticeId, userId);
        } else {
            bookmarkDao.addBookmark(new Bookmark(noticeId, userId, System.currentTimeMillis()));
        }
    }

    public Notice createNotice(
            String title,
            String content,
            NoticeCategory category,
            NoticePriority priority,
            User author,
            List<String> targets,
            boolean pinned,
            NoticeStatus status,
            String attachmentsJson,
            List<Map.Entry<String, byte[]>> pendingAttachments
    ) {
        String serializedTargets = AudienceEngine.serializeTargets(targets);
        long now = System.currentTimeMillis();
        String noticeId = "not_" + UUID.randomUUID().toString().substring(0, 8);

        List<Attachment> savedAttachments = new ArrayList<>();
        if (pendingAttachments != null) {
            for (Map.Entry<String, byte[]> entry : pendingAttachments) {
                try {
                    Attachment att = attachmentService.uploadAttachment(
                            author,
                            noticeId,
                            entry.getKey(),
                            "application/octet-stream",
                            entry.getValue()
                    );
                    savedAttachments.add(att);
                } catch (Exception ignored) {
                }
            }
        }

        String finalAttachmentsJson = attachmentsJson != null ? attachmentsJson : "[]";
        if (!savedAttachments.isEmpty()) {
            JSONArray jsonArray = new JSONArray();
            for (Attachment att : savedAttachments) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("id", att.getId());
                    obj.put("fileName", att.getOriginalFileName());
                    obj.put("fileType", att.getFileExtension().toUpperCase());
                    obj.put("fileSize", att.getFileSizeFormatted());
                    obj.put("storageKey", att.getStorageKey());
                    jsonArray.put(obj);
                } catch (Exception ignored) {
                }
            }
            finalAttachmentsJson = jsonArray.toString();
        }

        Notice newNotice = new Notice(
                noticeId,
                title != null ? title.trim() : "",
                content != null ? content.trim() : "",
                category != null ? category : NoticeCategory.GENERAL,
                priority != null ? priority : NoticePriority.NORMAL,
                author.getName(),
                author.getId(),
                serializedTargets,
                status != null ? status : NoticeStatus.ACTIVE,
                pinned,
                true,
                now,
                now,
                null,
                null,
                finalAttachmentsJson,
                true
        );

        noticeDao.insertNotice(newNotice);

        AuditLog log = new AuditLog(
                "aud_" + UUID.randomUUID().toString().substring(0, 8),
                author.getId(),
                author.getName(),
                "CREATE_NOTICE",
                "Notice",
                noticeId,
                serializedTargets,
                now,
                "Title: '" + title + "', Priority: " + priority + ", Category: " + category + ", Attachments: " + savedAttachments.size()
        );
        auditLogDao.insertAuditLog(log);

        return newNotice;
    }

    public void updateNotice(Notice notice, User author) {
        notice.setUpdatedAt(System.currentTimeMillis());
        noticeDao.updateNotice(notice);

        AuditLog log = new AuditLog(
                "aud_" + UUID.randomUUID().toString().substring(0, 8),
                author.getId(),
                author.getName(),
                "UPDATE_NOTICE",
                "Notice",
                notice.getId(),
                notice.getTargetAudience(),
                System.currentTimeMillis(),
                "Updated title: '" + notice.getTitle() + "', status: " + notice.getStatus()
        );
        auditLogDao.insertAuditLog(log);
    }

    public void toggleNoticePin(String noticeId, boolean pinned, User author) {
        noticeDao.toggleNoticePin(noticeId, pinned);
        AuditLog log = new AuditLog(
                "aud_" + UUID.randomUUID().toString().substring(0, 8),
                author.getId(),
                author.getName(),
                pinned ? "PIN_NOTICE" : "UNPIN_NOTICE",
                "Notice",
                noticeId,
                "",
                System.currentTimeMillis(),
                "Pinned status changed to " + pinned
        );
        auditLogDao.insertAuditLog(log);
    }

    public void archiveNotice(String noticeId, User author) {
        noticeDao.updateNoticeStatus(noticeId, NoticeStatus.ARCHIVED, System.currentTimeMillis());
        AuditLog log = new AuditLog(
                "aud_" + UUID.randomUUID().toString().substring(0, 8),
                author.getId(),
                author.getName(),
                "ARCHIVE_NOTICE",
                "Notice",
                noticeId,
                "",
                System.currentTimeMillis(),
                "Notice moved to institutional archive"
        );
        auditLogDao.insertAuditLog(log);
    }

    public void deleteNotice(String noticeId, User author) {
        attachmentService.cleanupNoticeAttachments(noticeId);
        noticeDao.deleteNotice(noticeId);
        AuditLog log = new AuditLog(
                "aud_" + UUID.randomUUID().toString().substring(0, 8),
                author.getId(),
                author.getName(),
                "DELETE_NOTICE",
                "Notice",
                noticeId,
                "",
                System.currentTimeMillis(),
                "Notice and associated attachments permanently deleted by administrator"
        );
        auditLogDao.insertAuditLog(log);
    }

    public void setUserActiveStatus(String userId, boolean active, User author) {
        userDao.setUserActiveStatus(userId, active);
        AuditLog log = new AuditLog(
                "aud_" + UUID.randomUUID().toString().substring(0, 8),
                author.getId(),
                author.getName(),
                active ? "ACTIVATE_USER" : "DEACTIVATE_USER",
                "User",
                userId,
                "",
                System.currentTimeMillis(),
                "User active state set to " + active
        );
        auditLogDao.insertAuditLog(log);
    }

    public User getUserByEmail(String email) {
        if (email == null) return null;
        return userDao.getUserByEmail(email.trim().toLowerCase());
    }

    public User authenticateUser(String email, String password) {
        if (email == null || password == null) return null;
        String cleanEmail = email.trim().toLowerCase();
        String cleanPassword = password.trim();
        if (cleanEmail.isEmpty() || cleanPassword.isEmpty()) return null;

        User user = userDao.getUserByEmail(cleanEmail);
        if (user != null && user.getPassword().equals(cleanPassword) && user.isActive()) {
            return user;
        }
        return null;
    }

    public User registerUser(User user) {
        user.setRole(Role.STUDENT);
        user.setActive(true);
        userDao.insertUser(user);

        AuditLog log = new AuditLog(
                "aud_" + UUID.randomUUID().toString().substring(0, 8),
                user.getId(),
                user.getName(),
                "USER_REGISTER",
                "User",
                user.getId(),
                user.getDepartment() + " | " + user.getRole().getDisplayName(),
                System.currentTimeMillis(),
                "Self-registered new student account: " + user.getEmail()
        );
        auditLogDao.insertAuditLog(log);
        return user;
    }

    public void seedIfEmpty() {
        SmartCampusDatabase.populateInitialData(database);
    }
}
