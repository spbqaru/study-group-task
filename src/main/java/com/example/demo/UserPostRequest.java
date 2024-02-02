package com.example.demo;

public class UserPostRequest {
    private final int studyGroupId;
    private final int userId;

    public UserPostRequest(int studyGroupId, int userId) {
        this.studyGroupId = studyGroupId;
        this.userId = userId;
    }

    public int getStudyGroupId() {
        return studyGroupId;
    }

    public int getUserId() {
        return userId;
    }
}
