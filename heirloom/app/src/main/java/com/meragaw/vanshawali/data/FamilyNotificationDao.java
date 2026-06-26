package com.meragaw.vanshawali.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.meragaw.vanshawali.model.FamilyNotification;

import java.util.List;

@Dao
public interface FamilyNotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FamilyNotification notification);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FamilyNotification> notifications);

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    LiveData<List<FamilyNotification>> getAll();

    @Query("UPDATE notifications SET isRead = 1")
    void markAllRead();
}
