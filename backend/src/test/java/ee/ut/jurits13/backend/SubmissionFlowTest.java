package ee.ut.jurits13.backend;

import ee.ut.jurits13.backend.entity.UserRole;
import ee.ut.jurits13.backend.repository.ExerciseRepository;
import ee.ut.jurits13.backend.repository.SubmissionRepository;
import ee.ut.jurits13.backend.repository.UserRepository;
import ee.ut.jurits13.backend.entity.Exercise;
import ee.ut.jurits13.backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SubmissionFlowTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    SubmissionRepository submissionRepository;

    private Long userId;
    private Long exerciseId;

    @BeforeEach
    void setup() {
        submissionRepository.deleteAll();
        exerciseRepository.deleteAll();
        userRepository.deleteAll();

        User user = userRepository.save(new User("student1", UserRole.STUDENT));
        Exercise exercise = exerciseRepository.save(
                new Exercise("HTML Basics", "Create a page", 1)
        );

        userId = user.getId();
        exerciseId = exercise.getId();
    }

    @Test
    void creatingSubmissionGeneratesStubFeedback() throws Exception {
        mvc.perform(post("/api/submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "exerciseId": %d,
                                  "userId": %d,
                                  "answer": "<h1>Hello</h1><p>Test</p>"
                                }
                                """.formatted(exerciseId, userId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.feedback").exists())
                .andExpect(jsonPath("$.feedback").value(org.hamcrest.Matchers.containsString("Coach feedback")))
                .andExpect(jsonPath("$.llmModel").value("stub"))
                .andExpect(jsonPath("$.promptVersion").value("v1"))
                .andExpect(jsonPath("$.evaluatedAt").exists());
    }

    @Test
    void missingUserReturns404() throws Exception {
        mvc.perform(post("/api/submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "exerciseId": %d,
                                  "userId": 99999,
                                  "answer": "x"
                                }
                                """.formatted(exerciseId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }
}
