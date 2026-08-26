package com.example.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.backend.AttachmentController;
import com.example.backend.AttachmentService;
import com.example.backend.FileStorageService;
import com.example.data.CampusRepository;
import com.example.data.SmartCampusDatabase;
import com.example.model.Attachment;
import com.example.model.AuditLog;
import com.example.model.Department;
import com.example.model.Notice;
import com.example.model.NoticeCategory;
import com.example.model.NoticePriority;
import com.example.model.NoticeStatus;
import com.example.model.NoticeWithState;
import com.example.model.Role;
import com.example.model.User;
import com.example.service.AudienceEngine;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;

public class SmartCampusViewModel extends AndroidViewModel {

    private final SmartCampusDatabase database;
    private final CampusRepository repository;
    private final com.example.service.FirebaseSyncService firebaseSyncService;
    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();

    // Authentication and Session
    private final MutableStateFlow<User> _currentUser = StateFlowKt.MutableStateFlow(null);
    private final MutableStateFlow<Boolean> _isLoggedIn = StateFlowKt.MutableStateFlow(false);

    // Navigation
    private final MutableStateFlow<AppScreen> _currentScreen = StateFlowKt.MutableStateFlow(AppScreen.AUTH);
    private final MutableStateFlow<String> _selectedNoticeId = StateFlowKt.MutableStateFlow(null);

    // Filtering and Search
    private final MutableStateFlow<String> _searchQuery = StateFlowKt.MutableStateFlow("");
    private final MutableStateFlow<NoticeCategory> _selectedCategory = StateFlowKt.MutableStateFlow(null);
    private final MutableStateFlow<NoticePriority> _selectedPriority = StateFlowKt.MutableStateFlow(null);
    private final MutableStateFlow<InboxFilter> _inboxFilter = StateFlowKt.MutableStateFlow(InboxFilter.ALL);

    // Feedback and Modals
    private final MutableStateFlow<String> _snackbarMessage = StateFlowKt.MutableStateFlow(null);
    private final MutableStateFlow<NoticeAnalyticsData> _analyticsModalData = StateFlowKt.MutableStateFlow(null);
    private final MutableStateFlow<AttachmentPreviewModalData> _previewModalData = StateFlowKt.MutableStateFlow(null);

    // Notice Creation & Pending Uploads
    private final MutableStateFlow<List<PendingAttachment>> _pendingAttachments = StateFlowKt.MutableStateFlow(Collections.emptyList());
    private final MutableStateFlow<Boolean> _isUploading = StateFlowKt.MutableStateFlow(false);
    private final MutableStateFlow<Float> _uploadProgress = StateFlowKt.MutableStateFlow(0f);

    // Data Streams for UI
    private final MutableStateFlow<List<User>> _allUsers = StateFlowKt.MutableStateFlow(Collections.emptyList());
    private final MutableStateFlow<List<Department>> _allDepartments = StateFlowKt.MutableStateFlow(Collections.emptyList());
    private final MutableStateFlow<List<AuditLog>> _recentAuditLogs = StateFlowKt.MutableStateFlow(Collections.emptyList());
    private final MutableStateFlow<Integer> _activeStudentCount = StateFlowKt.MutableStateFlow(4);
    private final MutableStateFlow<List<Notice>> _adminAllNotices = StateFlowKt.MutableStateFlow(Collections.emptyList());
    private final MutableStateFlow<List<Attachment>> _allVaultAttachments = StateFlowKt.MutableStateFlow(Collections.emptyList());

    private final MutableStateFlow<List<NoticeWithState>> _rawPersonalizedNotices = StateFlowKt.MutableStateFlow(Collections.emptyList());
    private final MutableStateFlow<List<NoticeWithState>> _filteredNotices = StateFlowKt.MutableStateFlow(Collections.emptyList());
    private final MutableStateFlow<List<NoticeWithState>> _urgentNotices = StateFlowKt.MutableStateFlow(Collections.emptyList());
    private final MutableStateFlow<List<NoticeWithState>> _savedNotices = StateFlowKt.MutableStateFlow(Collections.emptyList());
    private final MutableStateFlow<Integer> _unreadCount = StateFlowKt.MutableStateFlow(0);

    public SmartCampusViewModel(@NonNull Application application) {
        super(application);
        this.database = SmartCampusDatabase.getDatabase(application);
        this.repository = new CampusRepository(database);
        this.firebaseSyncService = new com.example.service.FirebaseSyncService(repository);

        backgroundExecutor.execute(() -> {
            repository.seedIfEmpty();
            refreshDataInternal();
            firebaseSyncService.startRealtimeSync();
        });
    }

    // Getters for Compose StateFlow observing
    public StateFlow<User> getCurrentUser() { return _currentUser; }
    public StateFlow<Boolean> isLoggedIn() { return _isLoggedIn; }
    public StateFlow<AppScreen> getCurrentScreen() { return _currentScreen; }
    public StateFlow<String> getSelectedNoticeId() { return _selectedNoticeId; }
    public StateFlow<String> getSearchQuery() { return _searchQuery; }
    public StateFlow<NoticeCategory> getSelectedCategory() { return _selectedCategory; }
    public StateFlow<NoticePriority> getSelectedPriority() { return _selectedPriority; }
    public StateFlow<InboxFilter> getInboxFilter() { return _inboxFilter; }
    public StateFlow<String> getSnackbarMessage() { return _snackbarMessage; }
    public StateFlow<NoticeAnalyticsData> getAnalyticsModalData() { return _analyticsModalData; }
    public StateFlow<AttachmentPreviewModalData> getPreviewModalData() { return _previewModalData; }
    public StateFlow<List<PendingAttachment>> getPendingAttachments() { return _pendingAttachments; }
    public StateFlow<Boolean> isUploading() { return _isUploading; }
    public StateFlow<Float> getUploadProgress() { return _uploadProgress; }
    public StateFlow<List<User>> getAllUsers() { return _allUsers; }
    public StateFlow<List<Department>> getAllDepartments() { return _allDepartments; }
    public StateFlow<List<AuditLog>> getRecentAuditLogs() { return _recentAuditLogs; }
    public StateFlow<Integer> getActiveStudentCount() { return _activeStudentCount; }
    public StateFlow<List<Notice>> getAdminAllNotices() { return _adminAllNotices; }
    public StateFlow<List<Attachment>> getAllVaultAttachments() { return _allVaultAttachments; }
    public StateFlow<List<NoticeWithState>> getRawPersonalizedNotices() { return _rawPersonalizedNotices; }
    public StateFlow<List<NoticeWithState>> getFilteredNotices() { return _filteredNotices; }
    public StateFlow<List<NoticeWithState>> getUrgentNotices() { return _urgentNotices; }
    public StateFlow<List<NoticeWithState>> getSavedNotices() { return _savedNotices; }
    public StateFlow<Integer> getUnreadCount() { return _unreadCount; }

    public CampusRepository getRepository() {
        return repository;
    }

    private void refreshDataInternal() {
        try {
            User user = _currentUser.getValue();
            List<Department> depts = database.departmentDao().getAllDepartmentsDirect();
            if (depts != null) _allDepartments.setValue(depts);

            List<User> users = database.userDao().getAllUsersDirect();
            if (users != null) _allUsers.setValue(users);

            List<AuditLog> logs = database.auditLogDao().getRecentAuditLogsDirect();
            if (logs != null) _recentAuditLogs.setValue(logs);

            int studentCount = database.userDao().getActiveStudentCountDirect();
            _activeStudentCount.setValue(studentCount);

            List<Notice> adminNotices = repository.getAllNoticesForAdminDirect();
            if (adminNotices != null) _adminAllNotices.setValue(adminNotices);

            List<Attachment> vault = repository.getAttachmentDao().getAllAttachmentsDirect();
            if (vault != null) _allVaultAttachments.setValue(vault);

            if (user != null) {
                List<NoticeWithState> personalized = repository.getPersonalizedNoticesDirect(user);
                _rawPersonalizedNotices.setValue(personalized != null ? personalized : Collections.emptyList());
            } else {
                _rawPersonalizedNotices.setValue(Collections.emptyList());
            }

            applyFiltersInternal();
        } catch (Exception ignored) {
        }
    }

    private void applyFiltersInternal() {
        List<NoticeWithState> raw = _rawPersonalizedNotices.getValue();
        String query = _searchQuery.getValue();
        NoticeCategory category = _selectedCategory.getValue();
        NoticePriority priority = _selectedPriority.getValue();
        InboxFilter inbox = _inboxFilter.getValue();

        List<NoticeWithState> filtered = new ArrayList<>();
        List<NoticeWithState> urgent = new ArrayList<>();
        List<NoticeWithState> saved = new ArrayList<>();
        int unread = 0;

        if (raw != null) {
            for (NoticeWithState item : raw) {
                Notice n = item.getNotice();
                if (!item.isRead()) {
                    unread++;
                }
                if (n.getPriority() == NoticePriority.URGENT && !item.isRead()) {
                    urgent.add(item);
                }
                if (item.isBookmarked()) {
                    saved.add(item);
                }

                boolean matchesQuery = (query == null || query.trim().isEmpty())
                        || n.getTitle().toLowerCase().contains(query.toLowerCase())
                        || n.getContent().toLowerCase().contains(query.toLowerCase())
                        || n.getPostedBy().toLowerCase().contains(query.toLowerCase())
                        || n.getCategory().name().toLowerCase().contains(query.toLowerCase())
                        || n.getAttachmentsJson().toLowerCase().contains(query.toLowerCase());

                boolean matchesCategory = (category == null) || (n.getCategory() == category);
                boolean matchesPriority = (priority == null) || (n.getPriority() == priority);

                boolean matchesInbox = true;
                if (inbox == InboxFilter.UNREAD) {
                    matchesInbox = !item.isRead();
                } else if (inbox == InboxFilter.IMPORTANT) {
                    matchesInbox = (n.getPriority() == NoticePriority.IMPORTANT || n.getPriority() == NoticePriority.URGENT);
                } else if (inbox == InboxFilter.SAVED) {
                    matchesInbox = item.isBookmarked();
                }

                if (matchesQuery && matchesCategory && matchesPriority && matchesInbox) {
                    filtered.add(item);
                }
            }
        }

        _filteredNotices.setValue(filtered);
        _urgentNotices.setValue(urgent);
        _savedNotices.setValue(saved);
        _unreadCount.setValue(unread);
    }

    public void login(String email, String password, Function2<? super Boolean, ? super String, Unit> onResult) {
        backgroundExecutor.execute(() -> {
            String cleanEmail = email != null ? email.trim() : "";
            String cleanPassword = password != null ? password.trim() : "";

            if (cleanEmail.isEmpty()) {
                onResult.invoke(false, "Please enter your college email address");
                return;
            }
            if (cleanPassword.isEmpty()) {
                onResult.invoke(false, "Please enter your password");
                return;
            }

            User user = repository.authenticateUser(cleanEmail, cleanPassword);
            if (user != null) {
                _currentUser.setValue(user);
                _isLoggedIn.setValue(true);
                if (user.getRole() == Role.STUDENT) {
                    _currentScreen.setValue(AppScreen.STUDENT_HOME);
                } else {
                    _currentScreen.setValue(AppScreen.ADMIN_DASHBOARD);
                }
                _snackbarMessage.setValue("Welcome back, " + user.getName() + "!");
                refreshDataInternal();
                onResult.invoke(true, null);
            } else {
                onResult.invoke(false, "Invalid institutional credentials. Please verify your email and password.");
            }
        });
    }

    public void register(
            String name,
            String email,
            String password,
            String department,
            String year,
            String section,
            String regNo,
            Function2<? super Boolean, ? super String, Unit> onResult
    ) {
        backgroundExecutor.execute(() -> {
            if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                onResult.invoke(false, "Please complete all mandatory fields");
                return;
            }
            if (!email.contains("@")) {
                onResult.invoke(false, "Please enter a valid institutional email address");
                return;
            }

            User existing = repository.getUserByEmail(email.trim().toLowerCase());
            if (existing != null) {
                onResult.invoke(false, "An account with email '" + email + "' is already registered.");
                return;
            }

            String reg = (regNo != null && !regNo.trim().isEmpty()) ? regNo.trim() : "ID-" + (1000 + (int)(Math.random() * 9000));
            User newUser = new User(
                    "usr_" + UUID.randomUUID().toString().substring(0, 8),
                    name.trim(),
                    email.trim().toLowerCase(),
                    password.trim(),
                    reg,
                    Role.STUDENT,
                    department != null ? department : "CSE",
                    year != null ? year : "1st Year",
                    section != null ? section : "Section A",
                    "",
                    true,
                    System.currentTimeMillis()
            );

            User created = repository.registerUser(newUser);
            firebaseSyncService.syncUserToCloud(created);
            _currentUser.setValue(created);
            _isLoggedIn.setValue(true);
            _currentScreen.setValue(AppScreen.STUDENT_HOME);
            _snackbarMessage.setValue("Account created successfully for " + created.getName() + "!");
            refreshDataInternal();
            onResult.invoke(true, null);
        });
    }

    public void logout() {
        _currentUser.setValue(null);
        _isLoggedIn.setValue(false);
        _currentScreen.setValue(AppScreen.AUTH);
        _snackbarMessage.setValue("Logged out successfully");
        backgroundExecutor.execute(this::refreshDataInternal);
    }

    public void navigateTo(AppScreen screen, String noticeId) {
        if (!_isLoggedIn.getValue() && screen != AppScreen.AUTH) {
            _currentScreen.setValue(AppScreen.AUTH);
            return;
        }
        User user = _currentUser.getValue();
        if (user != null && user.getRole() == Role.STUDENT) {
            if (screen == AppScreen.ADMIN_DASHBOARD || screen == AppScreen.ADMIN_NOTICES
                    || screen == AppScreen.CREATE_NOTICE || screen == AppScreen.STUDENT_MANAGEMENT
                    || screen == AppScreen.AUDIT_LOGS) {
                _currentScreen.setValue(AppScreen.STUDENT_HOME);
                _snackbarMessage.setValue("Access restricted to authorized faculty & administrators");
                return;
            }
        }
        if (noticeId != null) {
            _selectedNoticeId.setValue(noticeId);
        }
        _currentScreen.setValue(screen);
    }

    public void navigateTo(AppScreen screen) {
        navigateTo(screen, null);
    }

    public void setSearchQuery(String query) {
        _searchQuery.setValue(query);
        backgroundExecutor.execute(this::applyFiltersInternal);
    }

    public void setCategoryFilter(NoticeCategory category) {
        _selectedCategory.setValue(category);
        backgroundExecutor.execute(this::applyFiltersInternal);
    }

    public void setPriorityFilter(NoticePriority priority) {
        _selectedPriority.setValue(priority);
        backgroundExecutor.execute(this::applyFiltersInternal);
    }

    public void setInboxFilter(InboxFilter filter) {
        _inboxFilter.setValue(filter);
        backgroundExecutor.execute(this::applyFiltersInternal);
    }

    public void clearSnackbar() {
        _snackbarMessage.setValue(null);
    }

    public void markAsRead(String noticeId) {
        backgroundExecutor.execute(() -> {
            User user = _currentUser.getValue();
            if (user != null && noticeId != null) {
                repository.markNoticeAsRead(noticeId, user.getId());
                refreshDataInternal();
            }
        });
    }

    public void toggleBookmark(String noticeId, boolean currentStatus) {
        backgroundExecutor.execute(() -> {
            User user = _currentUser.getValue();
            if (user != null && noticeId != null) {
                repository.toggleBookmark(noticeId, user.getId(), currentStatus);
                _snackbarMessage.setValue(currentStatus ? "Removed from Saved" : "Saved to Bookmarks");
                refreshDataInternal();
            }
        });
    }

    public void createNotice(
            String title,
            String content,
            NoticeCategory category,
            NoticePriority priority,
            List<String> targets,
            boolean pinned,
            NoticeStatus status,
            String attachmentsJson,
            Function0<Unit> onSuccess
    ) {
        if (title == null || title.trim().isEmpty()) {
            _snackbarMessage.setValue("Please enter an announcement title");
            return;
        }
        if (content == null || content.trim().isEmpty()) {
            _snackbarMessage.setValue("Please enter announcement content");
            return;
        }
        if (targets == null || targets.isEmpty()) {
            _snackbarMessage.setValue("Please select at least one target audience");
            return;
        }

        backgroundExecutor.execute(() -> {
            User user = _currentUser.getValue();
            if (user == null) return;
            try {
                Notice createdNotice = repository.createNotice(
                        title,
                        content,
                        category,
                        priority,
                        user,
                        targets,
                        pinned,
                        status != null ? status : NoticeStatus.ACTIVE,
                        attachmentsJson,
                        null
                );
                if (createdNotice != null) {
                    firebaseSyncService.publishNoticeToCloud(createdNotice, null);
                }
                List<String> compressed = AudienceEngine.compressTargets(targets);
                List<String> labels = new ArrayList<>();
                for (String t : compressed) {
                    labels.add(AudienceEngine.formatTargetLabel(t));
                }
                _snackbarMessage.setValue("Notice published to: " + String.join(", ", labels));
                refreshDataInternal();
                if (onSuccess != null) onSuccess.invoke();
            } catch (Exception e) {
                _snackbarMessage.setValue("Failed to publish notice: " + e.getMessage());
            }
        });
    }

    public void toggleNoticePin(Notice notice) {
        backgroundExecutor.execute(() -> {
            User user = _currentUser.getValue();
            if (user != null && notice != null) {
                repository.toggleNoticePin(notice.getId(), !notice.isPinned(), user);
                _snackbarMessage.setValue(!notice.isPinned() ? "Notice pinned to top" : "Notice unpinned");
                refreshDataInternal();
            }
        });
    }

    public void archiveNotice(String noticeId) {
        backgroundExecutor.execute(() -> {
            User user = _currentUser.getValue();
            if (user != null && noticeId != null) {
                repository.archiveNotice(noticeId, user);
                _snackbarMessage.setValue("Notice archived successfully");
                refreshDataInternal();
            }
        });
    }

    public void deleteNotice(String noticeId) {
        backgroundExecutor.execute(() -> {
            User user = _currentUser.getValue();
            if (user != null && noticeId != null) {
                repository.deleteNotice(noticeId, user);
                _snackbarMessage.setValue("Notice deleted");
                refreshDataInternal();
            }
        });
    }

    public void toggleUserActive(User user) {
        backgroundExecutor.execute(() -> {
            User currentUser = _currentUser.getValue();
            if (currentUser != null && user != null) {
                repository.setUserActiveStatus(user.getId(), !user.isActive(), currentUser);
                _snackbarMessage.setValue(user.getName() + " is now " + (!user.isActive() ? "Active" : "Inactive"));
                refreshDataInternal();
            }
        });
    }

    public void openAnalyticsModal(Notice notice) {
        backgroundExecutor.execute(() -> {
            if (notice == null) return;
            List<String> targets = AudienceEngine.parseTargets(notice.getTargetAudience());
            int targetedCount = AudienceEngine.estimateRecipients(targets, 240);
            int readCount = repository.getNoticeReadCount(notice.getId());
            int unreadCount = Math.max(0, targetedCount - readCount);
            float rate = targetedCount > 0 ? ((float) readCount / (float) targetedCount * 100f) : 0f;

            _analyticsModalData.setValue(new NoticeAnalyticsData(
                    notice,
                    targetedCount,
                    readCount,
                    unreadCount,
                    rate
            ));
        });
    }

    public void closeAnalyticsModal() {
        _analyticsModalData.setValue(null);
    }

    public boolean addPendingAttachment(String fileName, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            _snackbarMessage.setValue("Cannot attach an empty file (0 bytes).");
            return false;
        }
        if (bytes.length > FileStorageService.MAX_FILE_SIZE_BYTES) {
            _snackbarMessage.setValue("File too large: Maximum attachment size is 20 MB.");
            return false;
        }

        String sanitized = repository.getFileStorageService().sanitizeFileName(fileName);
        String ext = sanitized.contains(".") ? sanitized.substring(sanitized.lastIndexOf(".") + 1).toLowerCase() : "";

        if (FileStorageService.FORBIDDEN_EXTENSIONS.contains(ext)) {
            _snackbarMessage.setValue("Security rejection: Executable and script files (." + ext + ") are strictly forbidden.");
            return false;
        }

        if (!FileStorageService.ALLOWED_EXTENSIONS.contains(ext)) {
            _snackbarMessage.setValue("Unsupported format (." + ext + "). Supported: PDF, Word, PowerPoint, Excel, Images, TXT, CSV.");
            return false;
        }

        String formattedSize = repository.getFileStorageService().formatFileSize((long) bytes.length);
        PendingAttachment item = new PendingAttachment(
                UUID.randomUUID().toString(),
                sanitized,
                ext.toUpperCase(),
                formattedSize,
                (long) bytes.length,
                bytes
        );

        List<PendingAttachment> currentList = new ArrayList<>(_pendingAttachments.getValue());
        currentList.add(item);
        _pendingAttachments.setValue(currentList);
        _snackbarMessage.setValue("Attached: " + sanitized + " (" + formattedSize + ")");
        return true;
    }

    public void removePendingAttachment(String id) {
        List<PendingAttachment> currentList = new ArrayList<>(_pendingAttachments.getValue());
        List<PendingAttachment> filtered = new ArrayList<>();
        for (PendingAttachment item : currentList) {
            if (!item.getId().equals(id)) {
                filtered.add(item);
            }
        }
        _pendingAttachments.setValue(filtered);
    }

    public void clearPendingAttachments() {
        _pendingAttachments.setValue(Collections.emptyList());
    }

    public void publishNoticeWithAttachments(
            String title,
            String content,
            NoticeCategory category,
            NoticePriority priority,
            List<String> targets,
            boolean pinned,
            Function0<Unit> onSuccess
    ) {
        publishNoticeWithAttachments(title, content, category, priority, targets, pinned, NoticeStatus.ACTIVE, onSuccess);
    }

    public void publishNoticeWithAttachments(
            String title,
            String content,
            NoticeCategory category,
            NoticePriority priority,
            List<String> targets,
            boolean pinned,
            NoticeStatus status,
            Function0<Unit> onSuccess
    ) {
        if (title == null || title.trim().isEmpty()) {
            _snackbarMessage.setValue("Please enter an announcement title");
            return;
        }
        if (content == null || content.trim().isEmpty()) {
            _snackbarMessage.setValue("Please enter announcement content");
            return;
        }
        if (targets == null || targets.isEmpty()) {
            _snackbarMessage.setValue("Please select at least one target audience");
            return;
        }
        if (_isUploading.getValue()) return;

        backgroundExecutor.execute(() -> {
            _isUploading.setValue(true);
            _uploadProgress.setValue(0.2f);
            User user = _currentUser.getValue();
            if (user == null || user.getRole() == Role.STUDENT) {
                _isUploading.setValue(false);
                _snackbarMessage.setValue("403 FORBIDDEN: Only authorized administrators or faculty can publish notices.");
                return;
            }

            try {
                _uploadProgress.setValue(0.5f);
                List<Map.Entry<String, byte[]>> pendingList = new ArrayList<>();
                for (PendingAttachment pa : _pendingAttachments.getValue()) {
                    pendingList.add(new AbstractMap.SimpleEntry<>(pa.getFileName(), pa.getBytes()));
                }

                repository.createNotice(
                        title,
                        content,
                        category,
                        priority,
                        user,
                        targets,
                        pinned,
                        status != null ? status : NoticeStatus.ACTIVE,
                        "[]",
                        pendingList
                );

                _uploadProgress.setValue(1.0f);
                _pendingAttachments.setValue(Collections.emptyList());

                List<String> compressed = AudienceEngine.compressTargets(targets);
                List<String> labels = new ArrayList<>();
                for (String t : compressed) {
                    labels.add(AudienceEngine.formatTargetLabel(t));
                }
                _snackbarMessage.setValue("Announcement posted successfully to: " + String.join(", ", labels));
                refreshDataInternal();
                if (onSuccess != null) onSuccess.invoke();
            } catch (Exception e) {
                _snackbarMessage.setValue("Failed to post announcement: " + e.getMessage());
            } finally {
                _isUploading.setValue(false);
                _uploadProgress.setValue(0f);
            }
        });
    }

    public void downloadAttachment(Attachment attachment) {
        backgroundExecutor.execute(() -> {
            User user = _currentUser.getValue();
            if (attachment == null) return;
            AttachmentController.ApiResponse<?> response = repository.getAttachmentController().downloadAttachment(user, attachment.getId());
            if (response.isSuccess()) {
                _snackbarMessage.setValue("Downloaded " + attachment.getOriginalFileName() + " (" + attachment.getFileSizeFormatted() + ") to secure device vault.");
                refreshDataInternal();
            } else {
                _snackbarMessage.setValue(response.getMessage());
            }
        });
    }

    public void previewAttachment(Attachment attachment) {
        backgroundExecutor.execute(() -> {
            User user = _currentUser.getValue();
            if (attachment == null) return;
            AttachmentController.ApiResponse<AttachmentService.AttachmentPreviewResult> response = repository.getAttachmentController().previewAttachment(user, attachment.getId());
            if (response.isSuccess() && response.getData() != null) {
                AttachmentService.AttachmentPreviewResult res = response.getData();
                _previewModalData.setValue(new AttachmentPreviewModalData(
                        res.getAttachment(),
                        res.getSignedUrl(),
                        res.getFileBytes()
                ));
            } else {
                _snackbarMessage.setValue(response.getMessage());
            }
        });
    }

    public Notice getNoticeById(String noticeId) {
        if (noticeId == null) return null;
        try {
            return repository.getNoticeById(noticeId);
        } catch (Exception e) {
            return null;
        }
    }

    public StateFlow<List<Attachment>> getAttachmentsForNoticeFlow(String noticeId) {
        User user = _currentUser.getValue();
        List<Attachment> list;
        try {
            list = repository.getAttachmentsForNotice(user, noticeId);
        } catch (Exception e) {
            list = Collections.emptyList();
        }
        return StateFlowKt.MutableStateFlow(list != null ? list : Collections.emptyList());
    }

    public void closePreviewModal() {
        _previewModalData.setValue(null);
    }
}
