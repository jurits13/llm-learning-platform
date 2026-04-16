package ee.ut.jurits13.backend.llm;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("openai")
public class OpenAiLlmClient implements LlmClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiLlmClient.class);

    private final OpenAIClient client;
    private final String model;
    private final String promptVersion;

    public OpenAiLlmClient(
            @Value("${app.llm.model:gpt-5.4-mini}") String model,
            @Value("${app.llm.prompt-version:v1}") String promptVersion
    ) {
        this.client = OpenAIOkHttpClient.fromEnv();
        this.model = model;
        this.promptVersion = promptVersion;
    }

    @Override
    public LlmResponse generate(String systemPrompt, String userPrompt) {
        try {
            ResponseCreateParams params = ResponseCreateParams.builder()
                    .model(model)
                    .instructions(systemPrompt)
                    .input(userPrompt)
                    .build();

            Response response = client.responses().create(params);

            String content = extractOutputText(response);

            return new LlmResponse(content, model, promptVersion);

        } catch (Exception e) {
            log.error("LLM call failed", e);

            return new LlmResponse(
                    """
                    I’m having trouble generating a full coaching reply right now.

                    Let’s still work through the problem step by step.

                    What did you expect to happen, and what actually happened?

                    One useful next step is to inspect one small part of the code and compare the expected and actual behavior.

                    What is the first specific thing you would check?
                    """,
                    model,
                    promptVersion
            );
        }
    }

    private String extractOutputText(Response response) {
        String text = response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(outputText -> outputText.text())
                .collect(java.util.stream.Collectors.joining())
                .trim();

        if (text.isBlank()) {
            return """
                    I’m sorry — I could not generate a coaching response right now.

                    What did you expect to happen, and what actually happened?

                    What is one small thing you can inspect first?
                    """;
        }

        return text;
    }
}