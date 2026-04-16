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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HelpSessionControllerTest {

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

        testUser = userRepository.save(new User("teststudent", UserRole.STUDENT));
    }

    @Test
    void createHelpSession_returnsCreatedSession() throws Exception {
        String requestBody = """
                {
                  "userId": %d,
                  "title": "Button click not working",
                  "problemDescription": "My button does nothing when I click it.",
                  "codeSnippet": "const btn = document.querySelector('#btn');",
                  "whatTried": "I added an event listener and checked querySelector."
                }
                """.formatted(testUser.getId());

        mockMvc.perform(post("/api/help-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is("teststudent")))
                .andExpect(jsonPath("$.title", is("Button click not working")))
                .andExpect(jsonPath("$.problemDescription", is("My button does nothing when I click it.")))
                .andExpect(jsonPath("$.codeSnippet", is("const btn = document.querySelector('#btn');")))
                .andExpect(jsonPath("$.whatTried", is("I added an event listener and checked querySelector.")))
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void getHelpSessionById_returnsSession() throws Exception {
        String requestBody = """
                {
                  "userId": %d,
                  "title": "DOM issue",
                  "problemDescription": "The DOM does not update.",
                  "codeSnippet": "document.querySelector('#msg').textContent = 'Hi';",
                  "whatTried": "I checked the selector."
                }
                """.formatted(testUser.getId());

        String response = mockMvc.perform(post("/api/help-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long sessionId = extractId(response);

        mockMvc.perform(get("/api/help-sessions/{id}", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) sessionId)))
                .andExpect(jsonPath("$.userId", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is("teststudent")))
                .andExpect(jsonPath("$.title", is("DOM issue")))
                .andExpect(jsonPath("$.problemDescription", is("The DOM does not update.")))
                .andExpect(jsonPath("$.codeSnippet", is("document.querySelector('#msg').textContent = 'Hi';")))
                .andExpect(jsonPath("$.whatTried", is("I checked the selector.")))
                .andExpect(jsonPath("$.status", is("OPEN")));
    }

    @Test
    void getSessionsByUserId_returnsUserSessions() throws Exception {
        String firstRequest = """
                {
                  "userId": %d,
                  "title": "First problem",
                  "problemDescription": "First description",
                  "codeSnippet": "console.log('first');",
                  "whatTried": "Tried one thing"
                }
                """.formatted(testUser.getId());

        String secondRequest = """
                {
                  "userId": %d,
                  "title": "Second problem",
                  "problemDescription": "Second description",
                  "codeSnippet": "console.log('second');",
                  "whatTried": "Tried another thing"
                }
                """.formatted(testUser.getId());

        mockMvc.perform(post("/api/help-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstRequest))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/help-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondRequest))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/help-sessions/user/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$[1].userId", is(testUser.getId().intValue())));
    }

    @Test
    void createHelpSession_withoutTitle_returnsValidationError() throws Exception {
        String requestBody = """
                {
                  "userId": %d,
                  "problemDescription": "Missing title example"
                }
                """.formatted(testUser.getId());

        mockMvc.perform(post("/api/help-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation failed")))
                .andExpect(jsonPath("$.fields.title").exists());
    }

    @Test
    void createHelpSession_withUnknownUser_returnsNotFound() throws Exception {
        String requestBody = """
                {
                  "userId": 999999,
                  "title": "Ghost user",
                  "problemDescription": "This should fail"
                }
                """;

        mockMvc.perform(post("/api/help-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User not found")));
    }

    @Test
    void createHelpSession_withOnlyPunctuationTitle_returnsValidationError() throws Exception {
        String requestBody = """
            {
              "userId": %d,
              "title": "....!!!???",
              "problemDescription": "My button does nothing when I click it."
            }
            """.formatted(testUser.getId());

        mockMvc.perform(post("/api/help-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation failed")))
                .andExpect(jsonPath("$.fields.title", is("Title must include at least one letter or number")));
    }

    @Test
    void createHelpSession_withOnlyPunctuationProblemDescription_returnsValidationError() throws Exception {
        String requestBody = """
            {
              "userId": %d,
              "title": "Button click not working",
              "problemDescription": "....!!!???"
            }
            """.formatted(testUser.getId());

        mockMvc.perform(post("/api/help-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation failed")))
                .andExpect(jsonPath("$.fields.problemDescription",
                        is("Problem description must include at least one letter or number")));
    }

    private long extractId(String json) {
        String idPart = json.replaceAll(".*\"id\":(\\d+).*", "$1");
        return Long.parseLong(idPart);
    }
}