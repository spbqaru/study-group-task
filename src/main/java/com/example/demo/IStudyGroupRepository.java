package com.example.demo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Repository;

@Repository
public interface IStudyGroupRepository {

    CompletableFuture<Object> createStudyGroups(StudyGroup studyGroup);

    CompletableFuture<List<StudyGroup>> getAllStudyGroups();

    CompletableFuture<List<StudyGroup>> searchStudyGroups(String subject);

    CompletableFuture<Object> joinStudyGroups(int studyGroupId, int userId);

    CompletableFuture<Object> leaveStudyGroup(int studyGroupId, int userId);
}
