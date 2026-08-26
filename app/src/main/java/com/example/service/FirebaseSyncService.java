package com.example.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.data.CampusRepository;
import com.example.model.Attachment;
import com.example.model.Notice;
import com.example.model.NoticeCategory;
import com.example.model.NoticePriority;
import com.example.model.NoticeStatus;
import com.example.model.Role;
import com.example.model.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Service responsible for bidirectional real-time synchronization between
 * the local Room database and Cloud Firestore.
 */
public class FirebaseSyncService {

    private static final String TAG = "FirebaseSyncService";
    private static final String COLLECTION_NOTICES = "notices";
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_ATTACHMENTS = "attachments";

    private final CampusRepository repository;
    private FirebaseFirestore firestore;
    private ListenerRegistration noticesListener;
    private boolean isInitialized = false;

    public interface SyncCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public FirebaseSyncService(CampusRepository repository) {
        this.repository = repository;
        initFirestore();
    }

    private void initFirestore() {
        try {
            if (com.example.MainActivity.getAppContext() != null &&
                !FirebaseApp.getApps(com.example.MainActivity.getAppContext()).isEmpty()) {
                firestore = FirebaseFirestore.getInstance();
                isInitialized = true;
                Log.d(TAG, "Firebase Firestore initialized successfully");
            } else {
                Log.w(TAG, "FirebaseApp is not initialized yet. Waiting for configuration.");
            }
        } catch (Exception e) {
            Log.w(TAG, "Firebase Firestore not available (requires google-services.json): " + e.getMessage());
            isInitialized = false;
        }
    }

    public boolean isConfigured() {
        if (!isInitialized) {
            initFirestore();
        }
        return isInitialized && firestore != null;
    }

    /**
     * Start real-time listening to cloud notices changes
     */
    public void startRealtimeSync() {
        if (!isConfigured()) {
            Log.w(TAG, "Cannot start real-time sync: Firestore not configured");
            return;
        }

        try {
            if (noticesListener != null) {
                noticesListener.remove();
            }

            noticesListener = firestore.collection(COLLECTION_NOTICES)
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            Log.e(TAG, "Listen failed on Firestore notices: " + e.getMessage());
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            new Thread(() -> {
                                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                    try {
                                        Notice notice = parseNoticeFromDoc(doc);
                                        if (notice != null) {
                                            repository.getNoticeDao().insertNotice(notice);
                                        }
                                    } catch (Exception ex) {
                                        Log.e(TAG, "Error importing Firestore notice document: " + ex.getMessage());
                                    }
                                }
                                Log.d(TAG, "Synced " + queryDocumentSnapshots.size() + " notices from Cloud Firestore");
                            }).start();
                        }
                    });
        } catch (Exception ex) {
            Log.e(TAG, "Failed to register real-time listener: " + ex.getMessage());
        }
    }

    public void stopRealtimeSync() {
        if (noticesListener != null) {
            noticesListener.remove();
            noticesListener = null;
        }
    }

    /**
     * Push a locally created notice up to the Cloud Firestore database
     */
    public void publishNoticeToCloud(Notice notice, SyncCallback callback) {
        if (!isConfigured()) {
            if (callback != null) callback.onError("Cloud sync in local mode (connect google-services.json for cloud sync)");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", notice.getId());
        data.put("title", notice.getTitle());
        data.put("content", notice.getContent());
        data.put("category", notice.getCategory().name());
        data.put("priority", notice.getPriority().name());
        data.put("postedBy", notice.getPostedBy());
        data.put("postedById", notice.getPostedById());
        data.put("targetAudience", notice.getTargetAudience());
        data.put("status", notice.getStatus().name());
        data.put("pinned", notice.isPinned());
        data.put("approved", notice.isApproved());
        data.put("createdAt", notice.getCreatedAt());
        data.put("updatedAt", notice.getUpdatedAt());
        data.put("scheduledAt", notice.getScheduledAt());
        data.put("expiresAt", notice.getExpiresAt());
        data.put("attachmentsJson", notice.getAttachmentsJson());
        data.put("isOfficial", notice.isOfficial());

        firestore.collection(COLLECTION_NOTICES).document(notice.getId())
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Notice successfully synced to Cloud Firestore: " + notice.getId());
                    if (callback != null) callback.onSuccess("Broadcasted to Cloud Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to sync notice to Cloud: " + e.getMessage());
                    if (callback != null) callback.onError(e.getMessage());
                });
    }

    /**
     * Upload / Register an attachment metadata in Cloud Firestore
     */
    public void publishAttachmentToCloud(Attachment attachment, SyncCallback callback) {
        if (!isConfigured()) return;

        Map<String, Object> data = new HashMap<>();
        data.put("id", attachment.getId());
        data.put("noticeId", attachment.getNoticeId());
        data.put("originalFileName", attachment.getOriginalFileName());
        data.put("storedFileName", attachment.getStoredFileName());
        data.put("contentType", attachment.getContentType());
        data.put("fileExtension", attachment.getFileExtension());
        data.put("fileSize", attachment.getFileSize());
        data.put("fileSizeFormatted", attachment.getFileSizeFormatted());
        data.put("storageKey", attachment.getStorageKey());
        data.put("uploadedBy", attachment.getUploadedBy());
        data.put("createdAt", attachment.getCreatedAt());
        data.put("downloadCount", attachment.getDownloadCount());

        firestore.collection(COLLECTION_ATTACHMENTS).document(attachment.getId())
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess("Attachment metadata synced to Cloud");
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e.getMessage());
                });
    }

    /**
     * Upload local user account to Cloud Firestore directory
     */
    public void syncUserToCloud(User user) {
        if (!isConfigured()) return;

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("name", user.getName());
        data.put("email", user.getEmail());
        data.put("registrationNumber", user.getRegistrationNumber());
        data.put("role", user.getRole().name());
        data.put("department", user.getDepartment());
        data.put("year", user.getYear());
        data.put("section", user.getSection());
        data.put("profileImage", user.getProfileImage());
        data.put("active", user.isActive());
        data.put("createdAt", user.getCreatedAt());

        firestore.collection(COLLECTION_USERS).document(user.getId())
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User synced to Cloud: " + user.getEmail()))
                .addOnFailureListener(e -> Log.e(TAG, "User cloud sync error: " + e.getMessage()));
    }

    private Notice parseNoticeFromDoc(DocumentSnapshot doc) {
        try {
            String id = doc.getString("id");
            if (id == null) id = doc.getId();
            String title = doc.getString("title");
            String content = doc.getString("content");
            String categoryStr = doc.getString("category");
            String priorityStr = doc.getString("priority");
            String postedBy = doc.getString("postedBy");
            String postedById = doc.getString("postedById");
            String targetAudience = doc.getString("targetAudience");
            String statusStr = doc.getString("status");
            Boolean pinned = doc.getBoolean("pinned");
            Boolean approved = doc.getBoolean("approved");
            Long createdAt = doc.getLong("createdAt");
            Long updatedAt = doc.getLong("updatedAt");
            Long scheduledAt = doc.getLong("scheduledAt");
            Long expiresAt = doc.getLong("expiresAt");
            String attachmentsJson = doc.getString("attachmentsJson");
            Boolean isOfficial = doc.getBoolean("isOfficial");

            NoticeCategory category = NoticeCategory.GENERAL;
            if (categoryStr != null) {
                try { category = NoticeCategory.valueOf(categoryStr); } catch (Exception ignored) {}
            }

            NoticePriority priority = NoticePriority.NORMAL;
            if (priorityStr != null) {
                try { priority = NoticePriority.valueOf(priorityStr); } catch (Exception ignored) {}
            }

            NoticeStatus status = NoticeStatus.ACTIVE;
            if (statusStr != null) {
                try { status = NoticeStatus.valueOf(statusStr); } catch (Exception ignored) {}
            }

            return new Notice(
                    id,
                    title != null ? title : "Untitled",
                    content != null ? content : "",
                    category,
                    priority,
                    postedBy != null ? postedBy : "Admin",
                    postedById != null ? postedById : "",
                    targetAudience != null ? targetAudience : "[\"ALL\"]",
                    status,
                    pinned != null ? pinned : false,
                    approved != null ? approved : true,
                    createdAt != null ? createdAt : System.currentTimeMillis(),
                    updatedAt != null ? updatedAt : System.currentTimeMillis(),
                    scheduledAt,
                    expiresAt,
                    attachmentsJson != null ? attachmentsJson : "[]",
                    isOfficial != null ? isOfficial : true
            );
        } catch (Exception e) {
            Log.e(TAG, "Failed parsing document to Notice: " + e.getMessage());
            return null;
        }
    }
}
