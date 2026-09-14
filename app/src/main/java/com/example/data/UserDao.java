package com.example.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users ORDER BY name ASC")
    LiveData<List<User>> getAllUsersLiveData();

    @Query("SELECT * FROM users ORDER BY name ASC")
    List<User> getAllUsersDirect();

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserById(String userId);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Query("SELECT COUNT(*) FROM users WHERE role = 'STUDENT' AND active = 1")
    LiveData<Integer> getActiveStudentCountLiveData();

    @Query("SELECT COUNT(*) FROM users WHERE role = 'STUDENT' AND active = 1")
    int getActiveStudentCountDirect();

    @Query("SELECT COUNT(*) FROM users")
    int getUserCountDirect();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(List<User> users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Query("UPDATE users SET active = :active WHERE id = :userId")
    void setUserActiveStatus(String userId, boolean active);
}
