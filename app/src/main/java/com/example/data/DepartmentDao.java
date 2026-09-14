package com.example.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.model.Department;

import java.util.List;

@Dao
public interface DepartmentDao {
    @Query("SELECT * FROM departments ORDER BY code ASC")
    LiveData<List<Department>> getAllDepartmentsLiveData();

    @Query("SELECT * FROM departments ORDER BY code ASC")
    List<Department> getAllDepartmentsDirect();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDepartments(List<Department> departments);
}
