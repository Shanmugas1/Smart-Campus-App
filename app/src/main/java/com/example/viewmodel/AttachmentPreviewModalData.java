package com.example.viewmodel;

import com.example.model.Attachment;

public class AttachmentPreviewModalData {
    private final Attachment attachment;
    private final String signedUrl;
    private final byte[] fileBytes;

    public AttachmentPreviewModalData(Attachment attachment, String signedUrl, byte[] fileBytes) {
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
