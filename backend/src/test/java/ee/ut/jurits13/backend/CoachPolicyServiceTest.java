package ee.ut.jurits13.backend;

import ee.ut.jurits13.backend.service.CoachPolicyResult;
import ee.ut.jurits13.backend.service.CoachPolicyService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class CoachPolicyServiceTest {

    private final CoachPolicyService coachPolicyService = new CoachPolicyService();

    @Test
    void emptyResponse_returnsFallbackAndEmptyResponseReason() {
        CoachPolicyResult result = coachPolicyService.apply("");

        assertTrue(result.filtered());
        assertEquals("empty_response", result.reason());
        assertNotNull(result.content());
        assertFalse(result.content().isBlank());
    }

    @Test
    void directSolutionPhrase_returnsTooDirect() {
        String raw = """
                Here is the solution.
                Use this code.
                """;

        CoachPolicyResult result = coachPolicyService.apply(raw);

        assertTrue(result.filtered());
        assertEquals("too_direct", result.reason());
    }

    @Test
    void longCodeBlock_returnsLongCodeBlock() {
        String raw = """
                Try this:
                ```javascript
                const btn = document.querySelector("#btn");
                btn.addEventListener("click", () => {
                  console.log("clicked");
                });
                document.body.style.background = "red";
                alert("done");
                ```
                What do you notice?
                """;

        CoachPolicyResult result = coachPolicyService.apply(raw);

        assertTrue(result.filtered());
        assertEquals("long_code_block", result.reason());
    }

    @Test
    void noQuestionAndNoGuidanceSignal_returnsNoGuidingQuestion() {
        String raw = """
            Good start.
            This explains the concept clearly.
            The method changes the object in place.
            """;

        CoachPolicyResult result = coachPolicyService.apply(raw);

        assertTrue(result.filtered());
        assertEquals("no_guiding_question", result.reason());
    }

    @Test
    void acceptableCoachingReply_isAccepted() {
        String raw = """
                Good start.

                What did you expect to happen when you clicked the button?

                One observation: querySelector only selects the element.

                What do you think btn contains right now?

                Next step: inspect whether the selector matches your HTML.
                """;

        CoachPolicyResult result = coachPolicyService.apply(raw);

        assertFalse(result.filtered());
        assertEquals("accepted", result.reason());
        assertEquals(raw.trim(), result.content());
    }
}