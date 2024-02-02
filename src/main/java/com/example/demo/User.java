package com.example.demo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    int userId;
    String name;

    public User(int userId, String name) {
        this.userId = userId;
        this.name = name;
    }

}
