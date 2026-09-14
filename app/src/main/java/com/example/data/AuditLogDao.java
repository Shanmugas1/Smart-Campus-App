package com.example.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.model.AuditLog;

import java.util.List;

@Dao
public interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    LiveData<List<AuditLog>> getRecentAuditLogsLiveData();

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    List<AuditLog> getRecentAuditLogsDirect();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAuditLog(AuditLog log);
}
