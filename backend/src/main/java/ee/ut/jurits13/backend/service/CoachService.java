package ee.ut.jurits13.backend.service;

import ee.ut.jurits13.backend.entity.HelpSession;
import ee.ut.jurits13.backend.entity.Message;
import ee.ut.jurits13.backend.llm.LlmClient;
import ee.ut.jurits13.backend.llm.LlmResponse;
import ee.ut.jurits13.backend.llm.PromptBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoachService {

    private final LlmClient llmClient;
    private final PromptBuilder promptBuilder;

    public CoachService(LlmClient llmClient, PromptBuilder promptBuilder) {
        this.llmClient = llmClient;
        this.promptBuilder = promptBuilder;
    }

    public CoachReply generateReply(HelpSession session, List<Message> messages) {
        String systemPrompt = promptBuilder.buildSystemPrompt();
        String userPrompt = promptBuilder.buildUserPrompt(session, messages);

        LlmResponse response = llmClient.generate(systemPrompt, userPrompt);

        return new CoachReply(
                response.content(),
                response.model(),
                response.promptVersion()
        );
    }

    public record CoachReply(String content, String llmModel, String promptVersion) {
    }
}
