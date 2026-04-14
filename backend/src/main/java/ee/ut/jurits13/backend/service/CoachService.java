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
    private final CoachPolicyService coachPolicyService;

    public CoachService(LlmClient llmClient, PromptBuilder promptBuilder, CoachPolicyService coachPolicyService) {
        this.llmClient = llmClient;
        this.promptBuilder = promptBuilder;
        this.coachPolicyService = coachPolicyService;
    }

    public CoachReply generateReply(HelpSession session, List<Message> messages) {
        String systemPrompt = promptBuilder.buildSystemPrompt();
        String userPrompt = promptBuilder.buildUserPrompt(session, messages);

        LlmResponse response = llmClient.generate(systemPrompt, userPrompt);

        CoachPolicyResult policyResult = coachPolicyService.apply(response.content());

        return new CoachReply(
                policyResult.content(),
                response.model(),
                response.promptVersion(),
                policyResult.filtered(),
                policyResult.reason()
        );
    }

    public record CoachReply(
            String content,
            String llmModel,
            String promptVersion,
            boolean filtered,
            String filterReason
    ) {
    }
}
