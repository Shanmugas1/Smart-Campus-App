package com.example.viewmodel;

public class PendingAttachment {
    private final String id;
    private final String fileName;
    private final String fileExtension;
    private final String fileSizeFormatted;
    private final long fileSizeBytes;
    private final byte[] bytes;

    public PendingAttachment(String id, String fileName, String fileExtension, String fileSizeFormatted, long fileSizeBytes, byte[] bytes) {
        this.id = id;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.fileSizeFormatted = fileSizeFormatted;
        this.fileSizeBytes = fileSizeBytes;
        this.bytes = bytes;
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getFileSizeFormatted() {
        return fileSizeFormatted;
    }

    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
