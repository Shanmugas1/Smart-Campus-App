package com.example.backend;

import com.example.model.Attachment;
import com.example.model.User;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Spring Boot REST Controller abstraction for Attachment operations.
 *
 * REST Mapping Architecture:
 * - POST   /api/attachments/upload              -> uploadAttachment()
 * - GET    /api/attachments/{id}/download       -> downloadAttachment()
 * - GET    /api/attachments/{id}/preview        -> previewAttachment()
 * - GET    /api/attachments/notice/{noticeId}   -> listNoticeAttachments()
 * - DELETE /api/attachments/{id}                -> deleteAttachment()
 */
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    public static class ApiResponse<T> {
        private final boolean success;
        private final T data;
        private final int statusCode;
        private final String message;

        private ApiResponse(boolean success, T data, int statusCode, String message) {
            this.success = success;
            this.data = data;
            this.statusCode = statusCode;
            this.message = message;
        }

        public static <T> ApiResponse<T> success(T data, String message) {
            return new ApiResponse<>(true, data, 200, message);
        }

        public static <T> ApiResponse<T> success(T data) {
            return new ApiResponse<>(true, data, 200, "Operation successful");
        }

        public static <T> ApiResponse<T> error(int statusCode, String message) {
            return new ApiResponse<>(false, null, statusCode, message);
        }

        public boolean isSuccess() {
            return success;
        }

        public T getData() {
            return data;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getMessage() {
            return message;
        }
    }

    public ApiResponse<Attachment> uploadAttachment(
            User user,
            String noticeId,
            String fileName,
            String contentType,
            byte[] fileBytes
    ) {
        if (user == null) {
            return ApiResponse.error(401, "Authentication required to upload attachments.");
        }
        try {
            Attachment attachment = attachmentService.uploadAttachment(
                    user,
                    noticeId,
                    fileName,
                    contentType,
                    fileBytes
            );
            return ApiResponse.success(attachment, "Attachment uploaded and secured in Cloud Storage.");
        } catch (SecurityException e) {
            return ApiResponse.error(403, e.getMessage() != null ? e.getMessage() : "Forbidden: Upload not authorized.");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage() != null ? e.getMessage() : "Invalid file payload.");
        } catch (Exception e) {
            return ApiResponse.error(500, "Storage error: " + e.getMessage());
        }
    }

    public ApiResponse<byte[]> downloadAttachment(
            User user,
            String attachmentId
    ) {
        if (user == null) {
            return ApiResponse.error(401, "Authentication required.");
        }
        try {
            byte[] bytes = attachmentService.getAttachmentForDownload(user, attachmentId);
            return ApiResponse.success(bytes, "Attachment fetched successfully.");
        } catch (SecurityException e) {
            int code = (e.getMessage() != null && e.getMessage().startsWith("401")) ? 401 : 403;
            return ApiResponse.error(code, e.getMessage() != null ? e.getMessage() : "Access Denied.");
        } catch (NoSuchElementException e) {
            return ApiResponse.error(404, e.getMessage() != null ? e.getMessage() : "Attachment not found.");
        } catch (Exception e) {
            return ApiResponse.error(500, "Download failed: " + e.getMessage());
        }
    }

    public ApiResponse<AttachmentService.AttachmentPreviewResult> previewAttachment(
            User user,
            String attachmentId
    ) {
        if (user == null) {
            return ApiResponse.error(401, "Authentication required.");
        }
        try {
            AttachmentService.AttachmentPreviewResult preview = attachmentService.getAttachmentPreview(user, attachmentId);
            return ApiResponse.success(preview, "Preview authorized.");
        } catch (SecurityException e) {
            int code = (e.getMessage() != null && e.getMessage().startsWith("401")) ? 401 : 403;
            return ApiResponse.error(code, e.getMessage() != null ? e.getMessage() : "Access Denied.");
        } catch (NoSuchElementException e) {
            return ApiResponse.error(404, e.getMessage() != null ? e.getMessage() : "Attachment not found.");
        } catch (Exception e) {
            return ApiResponse.error(500, "Preview failed: " + e.getMessage());
        }
    }

    public ApiResponse<List<Attachment>> listNoticeAttachments(
            User user,
            String noticeId
    ) {
        try {
            List<Attachment> list = attachmentService.getAttachmentsForNotice(user, noticeId);
            return ApiResponse.success(list);
        } catch (Exception e) {
            return ApiResponse.error(500, "Error listing attachments: " + e.getMessage());
        }
    }

    public ApiResponse<Void> deleteAttachment(
            User user,
            String attachmentId
    ) {
        if (user == null) {
            return ApiResponse.error(401, "Authentication required.");
        }
        try {
            attachmentService.deleteAttachment(user, attachmentId);
            return ApiResponse.success(null, "Attachment deleted.");
        } catch (SecurityException e) {
            return ApiResponse.error(403, e.getMessage() != null ? e.getMessage() : "Forbidden.");
        } catch (Exception e) {
            return ApiResponse.error(500, "Delete failed: " + e.getMessage());
        }
    }
}
