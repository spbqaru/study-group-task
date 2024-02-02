package com.example.demo;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public class StudyGroupRepository implements IStudyGroupRepository {

    @Override
    public CompletableFuture<Object> createStudyGroups(StudyGroup studyGroup) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<List<StudyGroup>> getAllStudyGroups() {
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public CompletableFuture<List<StudyGroup>> searchStudyGroups(String subject) {
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public CompletableFuture<Object> joinStudyGroups(int studyGroupId, int userId) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Object> leaveStudyGroup(int studyGroupId, int userId) {
        return CompletableFuture.completedFuture(null);
    }
}

