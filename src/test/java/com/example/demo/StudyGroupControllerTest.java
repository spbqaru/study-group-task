package com.example.demo;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;
import wiremock.com.fasterxml.jackson.databind.SerializationFeature;

import static com.example.demo.utils.JsonConversionUtils.getJson;
import static java.lang.String.valueOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudyGroupController.class)
class StudyGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IStudyGroupRepository studyGroupRepository;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createStudyGroupTest() throws Exception {
        StudyGroup studyGroup = new StudyGroup(1, "Math Study Group", Subject.Math, new DateTime(), List.of(new User(1, "Marth")));
        when(studyGroupRepository.createStudyGroups(any(StudyGroup.class))).thenReturn(CompletableFuture.completedFuture(null));
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mockMvc.perform(post("/studyGroups/create").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(studyGroup)))
                .andExpect(status().isOk());
        verify(studyGroupRepository).createStudyGroups(any(StudyGroup.class));
    }

    @ParameterizedTest
    @MethodSource("getStudyGroupProvider")
    @SneakyThrows
    void getAllStudyGroupsTest(List<StudyGroup> studyGroupList) {
        when(studyGroupRepository.getAllStudyGroups()).thenReturn(CompletableFuture.completedFuture(studyGroupList));
        MvcResult mockMvc1 = mockMvc.perform(get("/studyGroups/getAll").contentType(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();
        mockMvc.perform(asyncDispatch(mockMvc1))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("all.json")));
        verify(studyGroupRepository).getAllStudyGroups();
    }

    @ParameterizedTest
    @MethodSource("searchStudyGroupProvider")
    @SneakyThrows
    void searchStudyGroupsTest(List<StudyGroup> studyGroupList) {
        String searchParam = Subject.Math.toString();
        when(studyGroupRepository.searchStudyGroups(searchParam)).thenReturn(CompletableFuture.completedFuture(studyGroupList));
        MvcResult mockMvc1 = mockMvc.perform(get("/studyGroups/search").contentType(MediaType.APPLICATION_JSON)
                                                     .param("subject", searchParam))
                .andExpect(request().asyncStarted())
                .andReturn();
        mockMvc.perform(asyncDispatch(mockMvc1))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("search.json")));
        verify(studyGroupRepository).searchStudyGroups(searchParam);
    }

    @ParameterizedTest
    @SneakyThrows
    @CsvSource({"1, 1"})
    void joinLeaveStudyGroups(int groupId, int userId) {
        when(studyGroupRepository.joinStudyGroups(groupId, userId)).thenReturn(CompletableFuture.completedFuture(null));
        mockMvc.perform(post("/studyGroups/join").contentType(MediaType.APPLICATION_JSON)
                                .param("studyGroupId", valueOf(groupId))
                                .param("userId", valueOf(userId)))
                .andExpect(status().isOk());
        verify(studyGroupRepository).joinStudyGroups(groupId, userId);
    }

    @ParameterizedTest
    @SneakyThrows
    @CsvSource({"1, 1"})
    void testLeaveStudyGroups(int groupId, int userId) {
        when(studyGroupRepository.leaveStudyGroup(groupId, userId)).thenReturn(CompletableFuture.completedFuture(null));
        mockMvc.perform(post("/studyGroups/leave").contentType(MediaType.APPLICATION_JSON)
                                .param("studyGroupId", valueOf(groupId))
                                .param("userId", valueOf(userId)))
                .andExpect(status().isOk());
        verify(studyGroupRepository).leaveStudyGroup(groupId, userId);
    }

    private static Stream<List<StudyGroup>> getStudyGroupProvider() {
        StudyGroup studyGroup1 = new StudyGroup(1, "Math Study Group", Subject.Math, new DateTime(), List.of(new User(1, "Student1")));
        StudyGroup studyGroup2 = new StudyGroup(2, "Physics Study Group", Subject.Physics, new DateTime(), List.of(new User(2, "Student2")));
        StudyGroup studyGroup3 = new StudyGroup(3, "Chemistry Study Group", Subject.Chemistry, new DateTime(), List.of(new User(3, "Student3")));
        return Stream.of(Arrays.asList(studyGroup1, studyGroup2, studyGroup3));
    }

    private static Stream<List<StudyGroup>> searchStudyGroupProvider() {
        StudyGroup studyGroup1 = new StudyGroup(1, "Math Study Group", Subject.Math, new DateTime(), List.of(new User(1, "Student1")));
        StudyGroup studyGroup2 = new StudyGroup(2, "Math Study Group", Subject.Math, new DateTime(), List.of(new User(2, "Student2")));
        return Stream.of(Arrays.asList(studyGroup1, studyGroup2));
    }
}
