package com.meragaw.vanshawali.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.meragaw.vanshawali.data.HeirloomRepository;
import com.meragaw.vanshawali.model.FamilyMember;
import com.meragaw.vanshawali.model.FamilyNotification;
import com.meragaw.vanshawali.model.Memory;

import java.util.List;

/** Shared, activity-scoped view of the family data — observed by every data-driven fragment. */
public class FamilyViewModel extends AndroidViewModel {

    private final HeirloomRepository repository;

    private final LiveData<List<FamilyMember>> allMembers;
    private final LiveData<List<FamilyNotification>> allNotifications;
    private final LiveData<List<Memory>> photoMemories;
    private final LiveData<Integer> memberCount;
    private final LiveData<Integer> maxGeneration;
    private final LiveData<Integer> photoCount;

    public FamilyViewModel(@NonNull Application application) {
        super(application);
        repository = new HeirloomRepository(application);
        allMembers = repository.getAllMembers();
        allNotifications = repository.getAllNotifications();
        photoMemories = repository.getMemoriesWithPhotos();
        memberCount = repository.getMemberCount();
        maxGeneration = repository.getMaxGeneration();
        photoCount = repository.getPhotoCount();
    }

    public LiveData<List<FamilyMember>> getAllMembers() {
        return allMembers;
    }

    public LiveData<List<FamilyNotification>> getAllNotifications() {
        return allNotifications;
    }

    public LiveData<List<Memory>> getPhotoMemories() {
        return photoMemories;
    }

    public LiveData<Integer> getMemberCount() {
        return memberCount;
    }

    public LiveData<Integer> getMaxGeneration() {
        return maxGeneration;
    }

    public LiveData<Integer> getPhotoCount() {
        return photoCount;
    }

    public LiveData<FamilyMember> getMemberById(String id) {
        return repository.getMemberById(id);
    }

    public LiveData<List<FamilyMember>> getChildrenOf(String parentId) {
        return repository.getChildrenOf(parentId);
    }

    public LiveData<List<Memory>> getMemoriesForMember(String memberId) {
        return repository.getMemoriesForMember(memberId);
    }

    /** Adds a member relative to the current user (looked up from the latest emitted list). */
    public void addMember(FamilyMember member, String relation) {
        repository.addMemberWithRelation(member, relation, findCurrentUser(allMembers.getValue()));
    }

    public void addMemory(Memory memory) {
        repository.addMemory(memory);
    }

    public void markAllNotificationsRead() {
        repository.markAllNotificationsRead();
    }

    private FamilyMember findCurrentUser(List<FamilyMember> members) {
        if (members == null) return null;
        for (FamilyMember m : members) {
            if (m.isCurrentUser) return m;
        }
        return null;
    }
}
