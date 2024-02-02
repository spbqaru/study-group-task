package com.example.demo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StudyGroup {
    private final int studyGroupId;
    private final String name;
    private final Subject subject;
    private final DateTime createDate;
    private List<User> users;

    public StudyGroup(int studyGroupId, String name, Subject subject, DateTime createDate, List<User> users) {
        this.studyGroupId = studyGroupId;
        this.name = name;
        this.subject = subject;
        this.createDate = createDate;
        this.users = users;
    }

    public int getStudyGroupId() {
        return studyGroupId;
    }

    public String getName() {
        return name;
    }

    public Subject getSubject() {
        return subject;
    }

    public DateTime getCreateDate() {
        return createDate;
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }
}
