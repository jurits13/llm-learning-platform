package ee.ut.jurits13.backend;

import ee.ut.jurits13.backend.entity.User;
import ee.ut.jurits13.backend.entity.UserRole;
import ee.ut.jurits13.backend.llm.LlmClient;
import ee.ut.jurits13.backend.llm.LlmResponse;
import ee.ut.jurits13.backend.repository.HelpSessionRepository;
import ee.ut.jurits13.backend.repository.MessageRepository;
import ee.ut.jurits13.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FilteredCoachReplyPersistenceTest {

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

        testUser = userRepository.save(new User("filteredtestuser", UserRole.STUDENT));
    }

    @Test
    void filteredCoachReply_persistsPolicyMetadata() throws Exception {
        long sessionId = createHelpSession();

        String messageRequest = """
                {
                  "content": "My button does not work."
                }
                """;

        mockMvc.perform(post("/api/help-sessions/{id}/messages", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role", is("COACH")))
                .andExpect(jsonPath("$.filteredByPolicy", is(true)))
                .andExpect(jsonPath("$.policyReason", is("too_direct")))
                .andExpect(jsonPath("$.coachResponseLevel", is("LEVEL_1_REFLECTION")))
                .andExpect(jsonPath("$.llmModel", is("fake-test-model")))
                .andExpect(jsonPath("$.promptVersion", is("test-v-filtered")));

        mockMvc.perform(get("/api/help-sessions/{id}/messages", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role", is("STUDENT")))
                .andExpect(jsonPath("$[1].role", is("COACH")))
                .andExpect(jsonPath("$[1].filteredByPolicy", is(true)))
                .andExpect(jsonPath("$[1].policyReason", is("too_direct")))
                .andExpect(jsonPath("$[1].coachResponseLevel", is("LEVEL_1_REFLECTION")))
                .andExpect(jsonPath("$[1].llmModel", is("fake-test-model")))
                .andExpect(jsonPath("$[1].promptVersion", is("test-v-filtered")))
                .andExpect(jsonPath("$[1].content", not(blankOrNullString())));
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

    private long extractId(String json) {
        String idPart = json.replaceAll(".*\"id\":(\\d+).*", "$1");
        return Long.parseLong(idPart);
    }

    @TestConfiguration
    static class FakeFilteredLlmConfig {

        @Bean
        @Primary
        LlmClient fakeFilteredLlmClient() {
            return (systemPrompt, userPrompt) -> new LlmResponse(
                    """
                    Here is the solution.
                    Use this code.
                    """,
                    "fake-test-model",
                    "test-v-filtered"
            );
        }
    }
}