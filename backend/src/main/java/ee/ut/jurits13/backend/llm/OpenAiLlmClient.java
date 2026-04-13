package ee.ut.jurits13.backend.llm;

import org.springframework.stereotype.Service;

@Service
public class OpenAiLlmClient implements LlmClient {
    private final String apiKey = "YOUR_API_KEY";

    @Override
    public LlmResponse generate(String systemPrompt, String userPrompt) {

        // build HTTP request
        // send to API
        // parse response

        // String reply = callApi(systemPrompt, userPrompt);
        String reply = "Temporary real client placeholder";

        return new LlmResponse(reply, "gpt-model", "v1");
    }
}
