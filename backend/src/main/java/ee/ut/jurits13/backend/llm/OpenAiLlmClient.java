package ee.ut.jurits13.backend.llm;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Profile("openai")
public class OpenAiLlmClient implements LlmClient {

    private final String apiKey = System.getenv("OPENAI_API_KEY");
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public LlmResponse generate(String systemPrompt, String userPrompt) {
        try {
            String requestBody = """
                    {
                      "model": "gpt-5.4-mini",
                      "input": [
                        {"role": "system", "content": "%s"},
                        {"role": "user", "content": "%s"}
                      ]
                    }
                    """.formatted(
                    escape(systemPrompt),
                    escape(userPrompt)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/responses"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Status: " + response.statusCode());
            System.out.println("Body: " + response.body());

            String content = extractText(response.body());

            return new LlmResponse(content, "gpt-5.4-mini", "v1");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("LLM call failed", e);
        }
    }

    private String escape(String text) {
        return text.replace("\"", "\\\"").replace("\n", "\\n");
    }

    private String extractText(String json) {
        int typeIndex = json.indexOf("\"type\": \"output_text\"");
        if (typeIndex == -1) {
            return "No response from model";
        }

        int textIndex = json.indexOf("\"text\": \"", typeIndex);
        if (textIndex == -1) {
            return "No response from model";
        }

        textIndex += 9;
        int endIndex = json.indexOf("\"", textIndex);
        if (endIndex == -1) {
            return "No response from model";
        }

        return json.substring(textIndex, endIndex)
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\u2019", "’")
                .replace("\\u201c", "“")
                .replace("\\u201d", "”")
                .replace("\\u2014", "—");
    }
}
