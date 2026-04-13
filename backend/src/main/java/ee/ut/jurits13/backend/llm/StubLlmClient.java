package ee.ut.jurits13.backend.llm;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!openai")
public class StubLlmClient implements LlmClient {
    @Override
    public LlmResponse generate(String systemPrompt, String userPrompt) {
        String feedback = """
                I can see you are working through the problem.

                Observation:
                You have already made an attempt, which is a good start.

                Hint:
                Try to isolate one small part of the issue and verify what is happening there first.

                Guiding questions:
                - What result do you expect at this step?
                - What evidence do you have that the relevant code is actually running?

                Next step:
                Add one small check, print, or console log and compare the expected and actual behavior.
                """;

        return new LlmResponse(feedback, "stub", "v1");
    }
}
