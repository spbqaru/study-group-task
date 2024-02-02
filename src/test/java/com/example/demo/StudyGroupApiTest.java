package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import wiremock.com.fasterxml.jackson.core.JsonProcessingException;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;

import static com.example.demo.utils.JsonConversionUtils.getJson;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(WireMockExtension.class)
public class StudyGroupApiTest {
    @Value("${baseUrl}")
    private String baseUrl;

    WireMockServer wireMockServer = new WireMockServer();
    ObjectMapper objectMapper = new ObjectMapper();
    User creatorUser;
    User leaverUser;
    User joinerUser;

    @BeforeEach
    public void setup() {
        creatorUser = new User(1, "Creator");
        leaverUser = new User(2, "Leaver");
        joinerUser = new User(3, "Joiner");
        wireMockServer = new WireMockServer();
        wireMockServer.start();
    }

    @AfterEach
    public void teardown() {
        wireMockServer.stop();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("studyGroupsProvider")
    public void createStudyGroupTest(String testName, StudyGroup studyGroup, int status) throws JsonProcessingException {

        wireMockServer.stubFor(WireMock.post(urlEqualTo("/studyGroups/create"))
                                       .willReturn(aResponse().withHeader("Content-Type", "application/json")
                                                           .withStatus(status)
                                                           .withBody(objectMapper.writeValueAsString(studyGroup))));
        WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/studyGroups/create")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(status);
    }

    @Test
    void creationTimeIsRecordedTest() throws JsonProcessingException {

        StudyGroup expectedStudyGroup = new StudyGroup(1, "Math123", Subject.Math, new DateTime(), List.of(new User(1, "Student1")));

        wireMockServer.stubFor(WireMock.post(urlEqualTo("/studyGroups/create"))
                                       .willReturn(aResponse().withHeader("Content-Type", "application/json")
                                                           .withStatus(200)
                                                           .withBody(objectMapper.writeValueAsString(expectedStudyGroup))));
        WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/studyGroups/create")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(StudyGroup.class)
                .consumeWith(response -> {
                    StudyGroup createdStudyGroup = response.getResponseBody();
                    assertNotNull(createdStudyGroup);
                    assertNotNull(createdStudyGroup.getCreateDate());
                    assertTrue(createdStudyGroup.getCreateDate().createDate.isBefore(LocalDateTime.now()));
                });
    }

    @Test
    void usersCanCreateOnlyOneStudyGroupForSingleSubject() {
        Subject subject = Subject.Math;

        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/studyGroups/create"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(200)));

        StudyGroup firstStudyGroup = new StudyGroup(1, "Math123", subject, new DateTime(), List.of(creatorUser));

        WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/studyGroups/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(firstStudyGroup)
                .exchange()
                .expectStatus()
                .isOk();

        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/studyGroups/create"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(400)));
        StudyGroup secondStudyGroup = new StudyGroup(2, "Math345", subject, new DateTime(), List.of(creatorUser));

        WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/studyGroups/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(secondStudyGroup)
                .exchange()
                .expectStatus()
                .isBadRequest();

        wireMockServer.verify(WireMock.exactly(2), WireMock.postRequestedFor(WireMock.urlEqualTo("/studyGroups/create")));
    }

    @ParameterizedTest
    @MethodSource("subjectsProvider")
    void usersCanJoinStudyGroupForDifferentSubjects(Subject subject) {
        StudyGroup studyGroup = new StudyGroup(1, subject.toString(), subject, new DateTime(), List.of(creatorUser));
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/studyGroups/create"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(200)));
        WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/studyGroups/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(studyGroup)
                .exchange()
                .expectStatus()
                .isOk();

        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/studyGroups/join"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(200)));
        WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/studyGroups/join")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPostRequest(joinerUser.userId, studyGroup.getStudyGroupId()))
                .exchange()
                .expectStatus()
                .isEqualTo(200);
    }

    @ParameterizedTest
    @MethodSource("subjectsProvider")
    void usersJoinAlreadyJoinedStudyGroupInvalidTest(Subject subject) {
        StudyGroup studyGroup = new StudyGroup(1, subject.toString(), subject, new DateTime(), List.of(creatorUser));
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/studyGroups/create"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(200)));
        WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/studyGroups/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(studyGroup)
                .exchange()
                .expectStatus()
                .isOk();
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/studyGroups/join"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(200)));
        WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/studyGroups/join")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPostRequest(studyGroup.getStudyGroupId(), joinerUser.userId))
                .exchange()
                .expectStatus()
                .isEqualTo(200);

        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/studyGroups/join"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(409)));
        WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/studyGroups/join")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPostRequest(studyGroup.getStudyGroupId(), joinerUser.userId))
                .exchange()
                .expectStatus()
                .isEqualTo(409);
    }

    @ParameterizedTest
    @MethodSource("subjectsProvider")
    void usersCanLeaveStudyGroup(Subject subject) {
        StudyGroup studyGroup = new StudyGroup(1, subject.toString(), subject, new DateTime(), List.of(creatorUser));
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/studyGroups/create"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(200)));
        WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/studyGroups/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(studyGroup)
                .exchange()
                .expectStatus()
                .isOk();

        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/studyGroups/join"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(200)));
        WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/studyGroups/join")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPostRequest(leaverUser.userId, studyGroup.getStudyGroupId()))
                .exchange()
                .expectStatus()
                .isEqualTo(200);

        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/studyGroups/leave"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(200)));
        WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/studyGroups/leave")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPostRequest(leaverUser.userId, studyGroup.getStudyGroupId()))
                .exchange()
                .expectStatus()
                .isEqualTo(200);
    }

    @ParameterizedTest
    @MethodSource("subjectsProvider")
    void userLeavesAlreadyLeftGroup(Subject subject) {
        User creator = new User(1, "Creator");
        User leaver = new User(2, "Leaver");
        StudyGroup studyGroup = new StudyGroup(1, subject.toString(), subject, new DateTime(), List.of(creator));
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/studyGroups/create"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(200)));
        WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/studyGroups/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(studyGroup)
                .exchange()
                .expectStatus()
                .isOk();
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/studyGroups/leave"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(200)));
        WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/studyGroups/leave")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPostRequest(studyGroup.getStudyGroupId(), leaver.userId))
                .exchange()
                .expectStatus()
                .isEqualTo(200);
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/studyGroups/leave"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(409)));
        WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/studyGroups/leave")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPostRequest(studyGroup.getStudyGroupId(), leaver.userId))
                .exchange()
                .expectStatus()
                .isEqualTo(409);
    }

    @Test
    void userCanViewListOfStudyGroups() {
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/studyGroups/getAll"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(200)
                                                           .withHeader("Content-Type", "application/json")
                                                           .withBody(getJson("all.json"))));

        WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/studyGroups/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(StudyGroup.class)
                .hasSize(3);
    }

    static Stream<Arguments> studyGroupsProvider() {
        return Stream.of(Arguments.of("Valid study group name for Math subject ", new StudyGroup(1, "Math123", Subject.Math, new DateTime(), List.of(new User(1, "Student1"))), 200),
                         Arguments.of("Valid study group name for Chemistry subject with underscore",
                                      new StudyGroup(2, "Chemistry_Group", Subject.Chemistry, new DateTime(), List.of(new User(2, "Student2"))), 200),
                         Arguments.of("Valid study group name for Physics subject with dash", new StudyGroup(3, "Study-123", Subject.Physics, new DateTime(), List.of(new User(3, "Student3"))), 200),
                         Arguments.of("Valid study group name for Physics subject with alphanumeric characters",
                                      new StudyGroup(4, "Physics_1", Subject.Physics, new DateTime(), List.of(new User(4, "Student4"))), 200),
                         Arguments.of("Valid study group name with exactly 5 characters for Math subject", new StudyGroup(5, "ABCDE", Subject.Math, new DateTime(), List.of(new User(5, "Student5"))),
                                      200), Arguments.of("Valid study group name with exactly 30 characters for Math subject",
                                                         new StudyGroup(6, "MathGroup12345678901234567890123", Subject.Math, new DateTime(), List.of(new User(6, "Student6"))), 200),
                         Arguments.of("Invalid study group name with less than 5 characters", new StudyGroup(7, "Abc", Subject.Math, new DateTime(), List.of(new User(7, "Student7"))), 400),
                         Arguments.of("Invalid study group name with more than 30 characters",
                                      new StudyGroup(8, "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456", Subject.Chemistry, new DateTime(), List.of(new User(8, "Student8"))), 400),
                         Arguments.of("Invalid study group name with special characters", new StudyGroup(9, "Programming$Group", Subject.Physics, new DateTime(), List.of(new User(9, "Student9"))),
                                      400),
                         Arguments.of("Invalid study group name with whitespace", new StudyGroup(10, "Study Group", Subject.Physics, new DateTime(), List.of(new User(10, "Student10"))), 400),
                         Arguments.of("Invalid study group name with exactly 4 characters", new StudyGroup(11, "ABCD", Subject.Chemistry, new DateTime(), List.of(new User(11, "Student11"))), 400),
                         Arguments.of("Invalid study group name with exactly 31 characters",
                                      new StudyGroup(12, "MathGroup123456789012345678901234", Subject.Math, new DateTime(), List.of(new User(12, "Student12"))), 400));
    }

    static Stream<Arguments> subjectsProvider() {
        return Stream.of(Arguments.of(Subject.Math), Arguments.of(Subject.Physics), Arguments.of(Subject.Chemistry));
    }
}