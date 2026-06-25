package com.meragaw.vanshawali.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.meragaw.vanshawali.model.FamilyMember;

import java.util.List;

@Dao
public interface FamilyMemberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FamilyMember member);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FamilyMember> members);

    @Update
    void update(FamilyMember member);

    @Query("SELECT * FROM family_members ORDER BY generation ASC, createdAt ASC")
    LiveData<List<FamilyMember>> getAll();

    @Query("SELECT * FROM family_members WHERE id = :id LIMIT 1")
    LiveData<FamilyMember> getById(String id);

    @Query("SELECT * FROM family_members WHERE parentId = :parentId")
    LiveData<List<FamilyMember>> getChildrenOf(String parentId);

    @Query("SELECT * FROM family_members ORDER BY createdAt DESC LIMIT :limit")
    LiveData<List<FamilyMember>> getRecentlyAdded(int limit);

    @Query("SELECT COUNT(*) FROM family_members")
    LiveData<Integer> getCount();

    @Query("SELECT COUNT(*) FROM family_members")
    int getCountSync();

    @Query("SELECT MAX(generation) FROM family_members")
    LiveData<Integer> getMaxGeneration();
}
