package com.meragaw.vanshawali.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.meragaw.vanshawali.model.Memory;

import java.util.List;

@Dao
public interface MemoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Memory memory);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Memory> memories);

    @Query("SELECT * FROM memories WHERE memberId = :memberId ORDER BY date DESC")
    LiveData<List<Memory>> getForMember(String memberId);

    @Query("SELECT * FROM memories WHERE photoUri IS NOT NULL ORDER BY date DESC")
    LiveData<List<Memory>> getWithPhotos();

    @Query("SELECT COUNT(*) FROM memories WHERE photoUri IS NOT NULL")
    LiveData<Integer> getPhotoCount();
}
