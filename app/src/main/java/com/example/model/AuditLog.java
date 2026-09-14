package com.example.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(
        tableName = "audit_logs",
        indices = {@Index(value = {"timestamp"})}
)
public class AuditLog {
    @PrimaryKey
    @NonNull
    private String id;
    @NonNull
    private String userId;
    @NonNull
    private String userName;
    @NonNull
    private String action; // "CREATE_NOTICE", "UPDATE_NOTICE", "ARCHIVE_NOTICE", "DELETE_NOTICE", "ROLE_CHANGE"
    @NonNull
    private String resourceType; // "Notice", "User", "Department"
    @NonNull
    private String resourceId;
    @NonNull
    private String target;
    private long timestamp;
    @NonNull
    private String metadata;

    public AuditLog(@NonNull String id,
                    @NonNull String userId,
                    @NonNull String userName,
                    @NonNull String action,
                    @NonNull String resourceType,
                    @NonNull String resourceId,
                    @NonNull String target,
                    long timestamp,
                    @NonNull String metadata) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.target = target;
        this.timestamp = timestamp;
        this.metadata = metadata;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getUserName() {
        return userName;
    }

    public void setUserName(@NonNull String userName) {
        this.userName = userName;
    }

    @NonNull
    public String getAction() {
        return action;
    }

    public void setAction(@NonNull String action) {
        this.action = action;
    }

    @NonNull
    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(@NonNull String resourceType) {
        this.resourceType = resourceType;
    }

    @NonNull
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(@NonNull String resourceId) {
        this.resourceId = resourceId;
    }

    @NonNull
    public String getTarget() {
        return target;
    }

    public void setTarget(@NonNull String target) {
        this.target = target;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @NonNull
    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(@NonNull String metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLog auditLog = (AuditLog) o;
        return timestamp == auditLog.timestamp &&
                Objects.equals(id, auditLog.id) &&
                Objects.equals(userId, auditLog.userId) &&
                Objects.equals(userName, auditLog.userName) &&
                Objects.equals(action, auditLog.action) &&
                Objects.equals(resourceType, auditLog.resourceType) &&
                Objects.equals(resourceId, auditLog.resourceId) &&
                Objects.equals(target, auditLog.target) &&
                Objects.equals(metadata, auditLog.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, userName, action, resourceType, resourceId, target, timestamp, metadata);
    }
}
