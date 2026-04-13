package ee.ut.jurits13.backend.llm;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class StubLlmClient implements LlmClient {
    @Override
    public LlmResponse generate(String systemPrompt, String userPrompt) {

        String feedback = """
                I will help you as a learning coach.

                What to think about next:
                1) What exactly is the expected behavior?
                2) Which part of your current solution are you least sure about?
                3) Can you test one smaller part of the problem separately?

                Hint:
                - Try to explain the issue step by step in your own words before changing the code.

                Based on your latest message:
                "%s"
                """.formatted(userPrompt);

        return new LlmResponse(feedback, "stub", "v1");
    }
}
