package com.example.demo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/studyGroups")
public class StudyGroupController {
    private final IStudyGroupRepository studyGroupRepository;

    public StudyGroupController(IStudyGroupRepository studyGroupRepository) {
        this.studyGroupRepository = studyGroupRepository;
    }

    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<Object>> createStudyGroup(@RequestBody StudyGroup studyGroup) {
        return studyGroupRepository.createStudyGroups(studyGroup)
                .thenApplyAsync(result -> ResponseEntity.status(HttpStatus.OK)
                        .build())
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build());
    }

    @GetMapping("/getAll")
    public CompletableFuture<ResponseEntity<List<StudyGroup>>> getAllStudyGroups() {
        return studyGroupRepository.getAllStudyGroups()
                .thenApplyAsync(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build());
    }

    @GetMapping("/search")
    public CompletableFuture<ResponseEntity<List<StudyGroup>>> searchStudyGroups(@RequestParam String subject) {
        return studyGroupRepository.searchStudyGroups(subject)
                .thenApplyAsync(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build());
    }

    @PostMapping("/join")
    public CompletableFuture<ResponseEntity<Object>> joinStudyGroup(@RequestParam int studyGroupId, @RequestParam int userId) {
        return studyGroupRepository.joinStudyGroups(studyGroupId, userId)
                .thenApplyAsync(result -> ResponseEntity.status(HttpStatus.OK)
                        .build())
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build());
    }

    @PostMapping("/leave")
    public CompletableFuture<ResponseEntity<Object>> leaveStudyGroup(@RequestParam int studyGroupId, @RequestParam int userId) {
        return studyGroupRepository.leaveStudyGroup(studyGroupId, userId)
                .thenApplyAsync(result -> ResponseEntity.status(HttpStatus.OK)
                        .build())
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build());
    }
}

