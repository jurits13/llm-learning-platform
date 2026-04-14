package ee.ut.jurits13.backend.llm;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("openai")
public class OpenAiLlmClient implements LlmClient {

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
            throw new RuntimeException("LLM call failed", e);
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
            return "I’m sorry — I could not generate a coaching response right now.";
        }

        return text;
    }
}