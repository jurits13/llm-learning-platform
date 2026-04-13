package ee.ut.jurits13.backend.llm;

public interface LlmClient {
    LlmResponse generate(String systemPrompt, String userPrompt);
}
