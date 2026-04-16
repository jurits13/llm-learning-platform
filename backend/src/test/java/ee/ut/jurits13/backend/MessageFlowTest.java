package ee.ut.jurits13.backend;

import ee.ut.jurits13.backend.entity.User;
import ee.ut.jurits13.backend.entity.UserRole;
import ee.ut.jurits13.backend.repository.HelpSessionRepository;
import ee.ut.jurits13.backend.repository.MessageRepository;
import ee.ut.jurits13.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MessageFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HelpSessionRepository helpSessionRepository;

    @Autowired
    private MessageRepository messageRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        helpSessionRepository.deleteAll();
        userRepository.deleteAll();

        testUser = userRepository.save(new User("messagetestuser", UserRole.STUDENT));
    }

    @Test
    void postMessage_createsStudentAndCoachMessages() throws Exception {
        long sessionId = createHelpSession();

        String messageRequest = """
                {
                  "content": "I added an event listener but nothing happens."
                }
                """;

        mockMvc.perform(post("/api/help-sessions/{id}/messages", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.role", is("COACH")))
                .andExpect(jsonPath("$.content", containsString("Hint:")))
                .andExpect(jsonPath("$.content", containsString("Guiding questions:")))
                .andExpect(jsonPath("$.content", containsString("Next step:")))
                .andExpect(jsonPath("$.llmModel", is("stub")))
                .andExpect(jsonPath("$.promptVersion", is("v1")))
                .andExpect(jsonPath("$.createdAt").exists());

        mockMvc.perform(get("/api/help-sessions/{id}/messages", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].role", is("STUDENT")))
                .andExpect(jsonPath("$[0].content", is("I added an event listener but nothing happens.")))
                .andExpect(jsonPath("$[1].role", is("COACH")))
                .andExpect(jsonPath("$[1].llmModel", is("stub")))
                .andExpect(jsonPath("$[1].promptVersion", is("v1")));
    }

    @Test
    void getMessages_returnsConversationInCorrectOrder() throws Exception {
        long sessionId = createHelpSession();

        String firstMessage = """
                {
                  "content": "My button does not react when clicked."
                }
                """;

        String secondMessage = """
                {
                  "content": "I checked and querySelector returns null."
                }
                """;

        mockMvc.perform(post("/api/help-sessions/{id}/messages", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstMessage))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/help-sessions/{id}/messages", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondMessage))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/help-sessions/{id}/messages", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].role", is("STUDENT")))
                .andExpect(jsonPath("$[0].content", is("My button does not react when clicked.")))
                .andExpect(jsonPath("$[1].role", is("COACH")))
                .andExpect(jsonPath("$[2].role", is("STUDENT")))
                .andExpect(jsonPath("$[2].content", is("I checked and querySelector returns null.")))
                .andExpect(jsonPath("$[3].role", is("COACH")));
    }

    @Test
    void postingMessage_updatesSessionUpdatedAt() throws Exception {
        long sessionId = createHelpSession();

        String sessionBefore = mockMvc.perform(get("/api/help-sessions/{id}", sessionId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String updatedAtBefore = extractField(sessionBefore, "updatedAt");

        Thread.sleep(10);

        String messageRequest = """
                {
                  "content": "I added an event listener but nothing happens."
                }
                """;

        mockMvc.perform(post("/api/help-sessions/{id}/messages", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageRequest))
                .andExpect(status().isCreated());

        String sessionAfter = mockMvc.perform(get("/api/help-sessions/{id}", sessionId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String updatedAtAfter = extractField(sessionAfter, "updatedAt");

        org.junit.jupiter.api.Assertions.assertNotEquals(updatedAtBefore, updatedAtAfter);
    }

    @Test
    void postMessage_toUnknownSession_returnsNotFound() throws Exception {
        String messageRequest = """
                {
                  "content": "Hello"
                }
                """;

        mockMvc.perform(post("/api/help-sessions/{id}/messages", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Help session not found")));
    }

    @Test
    void postMessage_withBlankContent_returnsValidationError() throws Exception {
        long sessionId = createHelpSession();

        String messageRequest = """
                {
                  "content": ""
                }
                """;

        mockMvc.perform(post("/api/help-sessions/{id}/messages", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation failed")))
                .andExpect(jsonPath("$.fields.content").exists());
    }

    private long createHelpSession() throws Exception {
        String requestBody = """
                {
                  "userId": %d,
                  "title": "Button click not working",
                  "problemDescription": "My button does nothing when I click it.",
                  "codeSnippet": "const btn = document.querySelector('#btn');",
                  "whatTried": "I added an event listener and checked querySelector."
                }
                """.formatted(testUser.getId());

        String response = mockMvc.perform(post("/api/help-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return extractId(response);
    }

    @Test
    void postMessage_withOnlyPunctuation_returnsValidationError() throws Exception {
        long sessionId = createHelpSession();

        String messageRequest = """
            {
              "content": "....!!!!???"
            }
            """;

        mockMvc.perform(post("/api/help-sessions/{id}/messages", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation failed")))
                .andExpect(jsonPath("$.fields.content", is("Content must include at least one letter or number")));
    }

    private long extractId(String json) {
        String idPart = json.replaceAll(".*\"id\":(\\d+).*", "$1");
        return Long.parseLong(idPart);
    }

    private String extractField(String json, String fieldName) {
        String regex = ".*\\\"" + fieldName + "\\\":\\\"([^\\\"]+)\\\".*";
        return json.replaceAll(regex, "$1");
    }
}