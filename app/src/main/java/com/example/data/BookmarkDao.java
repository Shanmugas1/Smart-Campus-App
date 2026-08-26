package com.example.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.model.Bookmark;

import java.util.List;

@Dao
public interface BookmarkDao {
    @Query("SELECT * FROM bookmarks WHERE userId = :userId")
    LiveData<List<Bookmark>> getBookmarksForUserLiveData(String userId);

    @Query("SELECT * FROM bookmarks WHERE userId = :userId")
    List<Bookmark> getBookmarksForUserDirect(String userId);

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE noticeId = :noticeId AND userId = :userId)")
    LiveData<Boolean> isBookmarkedLiveData(String noticeId, String userId);

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE noticeId = :noticeId AND userId = :userId)")
    boolean isBookmarkedDirect(String noticeId, String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addBookmark(Bookmark bookmark);

    @Query("DELETE FROM bookmarks WHERE noticeId = :noticeId AND userId = :userId")
    void removeBookmark(String noticeId, String userId);
}
