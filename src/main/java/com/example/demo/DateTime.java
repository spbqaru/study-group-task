package com.example.demo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DateTime {
    LocalDateTime createDate;

    public DateTime() {
        this.createDate = LocalDateTime.now();
    }
}
