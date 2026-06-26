package com.meragaw.vanshawali.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.meragaw.vanshawali.model.FamilyMember;
import com.meragaw.vanshawali.model.FamilyNotification;
import com.meragaw.vanshawali.model.Memory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HeirloomRepository {

    private final FamilyMemberDao memberDao;
    private final FamilyNotificationDao notificationDao;
    private final MemoryDao memoryDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public HeirloomRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        memberDao = db.familyMemberDao();
        notificationDao = db.familyNotificationDao();
        memoryDao = db.memoryDao();
        seedIfEmpty();
    }

    // ── Family members ───────────────────────────────────────────────────────
    public LiveData<List<FamilyMember>> getAllMembers() {
        return memberDao.getAll();
    }

    public LiveData<FamilyMember> getMemberById(String id) {
        return memberDao.getById(id);
    }

    public LiveData<List<FamilyMember>> getChildrenOf(String parentId) {
        return memberDao.getChildrenOf(parentId);
    }

    public LiveData<List<FamilyMember>> getRecentlyAdded(int limit) {
        return memberDao.getRecentlyAdded(limit);
    }

    public LiveData<Integer> getMemberCount() {
        return memberDao.getCount();
    }

    public LiveData<Integer> getMaxGeneration() {
        return memberDao.getMaxGeneration();
    }

    /** Adds a new member, placing them in the tree relative to {@code anchor} (often the current user). */
    public void addMemberWithRelation(FamilyMember member, String relation, FamilyMember anchor) {
        member.createdAt = System.currentTimeMillis();
        executor.execute(() -> {
            if (anchor != null) {
                applyRelation(member, relation, anchor);
            }
            memberDao.insert(member);
        });
    }

    private void applyRelation(FamilyMember member, String relation, FamilyMember anchor) {
        switch (relation) {
            case "parent":
                member.generation = anchor.generation - 1;
                anchor.parentId = member.id;
                memberDao.update(anchor);
                break;
            case "spouse":
                member.generation = anchor.generation;
                member.spouseId = anchor.id;
                anchor.spouseId = member.id;
                memberDao.update(anchor);
                break;
            case "sibling":
                member.generation = anchor.generation;
                member.parentId = anchor.parentId;
                break;
            default: // child
                member.generation = anchor.generation + 1;
                member.parentId = anchor.id;
                break;
        }
    }

    // ── Notifications ─────────────────────────────────────────────────────────
    public LiveData<List<FamilyNotification>> getAllNotifications() {
        return notificationDao.getAll();
    }

    public void markAllNotificationsRead() {
        executor.execute(notificationDao::markAllRead);
    }

    // ── Memories ─────────────────────────────────────────────────────────────
    public LiveData<List<Memory>> getMemoriesForMember(String memberId) {
        return memoryDao.getForMember(memberId);
    }

    public LiveData<List<Memory>> getMemoriesWithPhotos() {
        return memoryDao.getWithPhotos();
    }

    public LiveData<Integer> getPhotoCount() {
        return memoryDao.getPhotoCount();
    }

    public void addMemory(Memory memory) {
        executor.execute(() -> memoryDao.insert(memory));
    }

    // ── Seed data (first run only) ──────────────────────────────────────────
    private void seedIfEmpty() {
        executor.execute(() -> {
            if (memberDao.getCountSync() > 0) return;
            memberDao.insertAll(SeedData.members());
            notificationDao.insertAll(SeedData.notifications());
            memoryDao.insertAll(SeedData.memories());
        });
    }
}
