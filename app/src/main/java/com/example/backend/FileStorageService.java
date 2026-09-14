package com.example.backend;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cloud Object Storage Service implementing strict file validation,
 * path traversal prevention, unique storage keys, and signed temporary URLs.
 */
public class FileStorageService {

    public static final long MAX_FILE_SIZE_BYTES = 20L * 1024L * 1024L; // 20 MB max

    // Allow-list for supported MIME types
    public static final Set<String> ALLOWED_MIME_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "image/jpeg",
            "image/png",
            "image/webp",
            "text/plain",
            "text/csv"
    )));

    // Allow-list for supported file extensions
    public static final Set<String> ALLOWED_EXTENSIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "pdf",
            "doc",
            "docx",
            "ppt",
            "pptx",
            "xls",
            "xlsx",
            "jpg",
            "jpeg",
            "png",
            "webp",
            "txt",
            "csv"
    )));

    // Strictly forbidden dangerous file extensions
    public static final Set<String> FORBIDDEN_EXTENSIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "exe", "bat", "cmd", "sh", "js", "html", "php", "jar",
            "scr", "ps1", "vbs", "py", "bin", "dll", "msi", "apk"
    )));

    private final ConcurrentHashMap<String, byte[]> cloudObjectBucket = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, SignedTokenInfo> signedUrlTokens = new ConcurrentHashMap<>();

    public static class StoredFileResult {
        private final String storedFileName;
        private final String storageKey;
        private final String originalFileName;
        private final String contentType;
        private final String fileExtension;
        private final long fileSize;
        private final String fileSizeFormatted;

        public StoredFileResult(String storedFileName,
                                String storageKey,
                                String originalFileName,
                                String contentType,
                                String fileExtension,
                                long fileSize,
                                String fileSizeFormatted) {
            this.storedFileName = storedFileName;
            this.storageKey = storageKey;
            this.originalFileName = originalFileName;
            this.contentType = contentType;
            this.fileExtension = fileExtension;
            this.fileSize = fileSize;
            this.fileSizeFormatted = fileSizeFormatted;
        }

        public String getStoredFileName() {
            return storedFileName;
        }

        public String getStorageKey() {
            return storageKey;
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public String getContentType() {
            return contentType;
        }

        public String getFileExtension() {
            return fileExtension;
        }

        public long getFileSize() {
            return fileSize;
        }

        public String getFileSizeFormatted() {
            return fileSizeFormatted;
        }
    }

    public static class SignedTokenInfo {
        private final String storageKey;
        private final String authorizedUserId;
        private final long expiryTime;

        public SignedTokenInfo(String storageKey, String authorizedUserId, long expiryTime) {
            this.storageKey = storageKey;
            this.authorizedUserId = authorizedUserId;
            this.expiryTime = expiryTime;
        }

        public String getStorageKey() {
            return storageKey;
        }

        public String getAuthorizedUserId() {
            return authorizedUserId;
        }

        public long getExpiryTime() {
            return expiryTime;
        }
    }

    /**
     * Validates and saves file in cloud object storage.
     * Enforces size check, extension check, MIME check, and path traversal prevention.
     */
    public StoredFileResult storeFile(
            String rawFileName,
            String rawContentType,
            byte[] fileBytes,
            String noticeId
    ) {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("Invalid file: File cannot be empty (0 bytes).");
        }

        if (fileBytes.length > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File too large: Maximum attachment size is 20 MB. Uploaded size: " + formatFileSize(fileBytes.length) + ".");
        }

        String sanitizedOriginalName = sanitizeFileName(rawFileName);
        String extension = getExtension(sanitizedOriginalName).toLowerCase(Locale.ROOT);

        if (FORBIDDEN_EXTENSIONS.contains(extension)) {
            throw new SecurityException("Security violation: Executable and script file types (." + extension + ") are strictly rejected.");
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Unsupported file type: '." + extension + "' is not allowed. Supported formats: PDF, Word, PowerPoint, Excel, Images, TXT, CSV.");
        }

        String normalizedMime = normalizeMimeType(rawContentType, extension);
        if (!ALLOWED_MIME_TYPES.contains(normalizedMime)) {
            throw new IllegalArgumentException("Invalid content type: MIME type '" + rawContentType + "' does not match allowed institutional document types.");
        }

        String uniqueUuid = UUID.randomUUID().toString();
        String safeStoredName = uniqueUuid + "." + extension;
        String storageKey = "vault/notices/" + noticeId + "/" + safeStoredName;

        cloudObjectBucket.put(storageKey, fileBytes);

        return new StoredFileResult(
                safeStoredName,
                storageKey,
                sanitizedOriginalName,
                normalizedMime,
                extension,
                fileBytes.length,
                formatFileSize(fileBytes.length)
        );
    }

    /**
     * Retrieves file bytes from secure cloud storage by storage key.
     */
    public byte[] retrieveFile(String storageKey) {
        byte[] bytes = cloudObjectBucket.get(storageKey);
        if (bytes == null) {
            throw new NoSuchElementException("File not found in cloud object storage: " + storageKey);
        }
        return bytes;
    }

    public String generateSignedUrl(String storageKey, String userId) {
        return generateSignedUrl(storageKey, userId, 15 * 60 * 1000L);
    }

    /**
     * Generates a short-lived cryptographically safe signed URL / token for authorized user.
     * Default TTL: 15 minutes.
     */
    public String generateSignedUrl(String storageKey, String userId, long ttlMillis) {
        String token = "signed_" + UUID.randomUUID().toString();
        long expiry = System.currentTimeMillis() + ttlMillis;
        signedUrlTokens.put(token, new SignedTokenInfo(storageKey, userId, expiry));
        return "https://storage.smartcampus.internal/" + storageKey + "?token=" + token + "&expires=" + expiry;
    }

    /**
     * Validates temporary signed token for download.
     */
    public String validateSignedToken(String token, String requestingUserId) {
        SignedTokenInfo info = signedUrlTokens.get(token);
        if (info == null) {
            throw new SecurityException("403 Forbidden: Invalid or expired signed access token.");
        }

        if (System.currentTimeMillis() > info.getExpiryTime()) {
            signedUrlTokens.remove(token);
            throw new SecurityException("403 Forbidden: Signed URL has expired. Please re-request from the announcement screen.");
        }

        if (!Objects.equals(info.getAuthorizedUserId(), requestingUserId)) {
            throw new SecurityException("403 Forbidden: Signed token does not belong to the requesting user session.");
        }

        return info.getStorageKey();
    }

    public boolean deleteFile(String storageKey) {
        return cloudObjectBucket.remove(storageKey) != null;
    }

    public void deleteFilesForNotice(String noticeId) {
        String prefix = "vault/notices/" + noticeId + "/";
        List<String> keysToRemove = new ArrayList<>();
        for (String key : cloudObjectBucket.keySet()) {
            if (key.startsWith(prefix)) {
                keysToRemove.add(key);
            }
        }
        for (String key : keysToRemove) {
            cloudObjectBucket.remove(key);
        }
    }

    public String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "attachment_document.bin";
        }
        String clean = fileName.replace("\\", "/");
        clean = new File(clean).getName();
        clean = clean.replace("..", "")
                .replace("/", "")
                .replace("\u0000", "")
                .trim();

        if (clean.isEmpty() || clean.equals(".") || clean.equals("..")) {
            return "attachment_document.bin";
        }
        return clean;
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    private String normalizeMimeType(String providedMime, String extension) {
        if (providedMime != null && !providedMime.trim().isEmpty() &&
                !providedMime.equals("application/octet-stream") &&
                ALLOWED_MIME_TYPES.contains(providedMime)) {
            return providedMime;
        }
        switch (extension.toLowerCase(Locale.ROOT)) {
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "webp":
                return "image/webp";
            case "txt":
                return "text/plain";
            case "csv":
                return "text/csv";
            default:
                return (providedMime != null && !providedMime.trim().isEmpty()) ? providedMime : "application/octet-stream";
        }
    }

    public String formatFileSize(long bytes) {
        if (bytes <= 0) return "0 B";
        String[] units = new String[]{"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024.0));
        if (digitGroups >= units.length) digitGroups = units.length - 1;
        DecimalFormat format = new DecimalFormat("#,##0.#");
        return format.format(bytes / Math.pow(1024.0, digitGroups)) + " " + units[digitGroups];
    }
}
