package ee.ut.jurits13.backend.service;

import ee.ut.jurits13.backend.entity.CoachResponseLevel;
import ee.ut.jurits13.backend.entity.HelpSession;
import ee.ut.jurits13.backend.entity.Message;
import ee.ut.jurits13.backend.entity.MessageRole;
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
    private final CoachLevelService coachLevelService;

    public CoachService(
            LlmClient llmClient,
            PromptBuilder promptBuilder,
            CoachPolicyService coachPolicyService,
            CoachLevelService coachLevelService
    ) {
        this.llmClient = llmClient;
        this.promptBuilder = promptBuilder;
        this.coachPolicyService = coachPolicyService;
        this.coachLevelService = coachLevelService;
    }

    public CoachReply generateReply(HelpSession session, List<Message> messages) {
        String latestStudentMessage = extractLatestStudentMessage(messages);
        CoachResponseLevel level = coachLevelService.determineLevel(messages, latestStudentMessage);

        String systemPrompt = promptBuilder.buildSystemPrompt(level);
        String userPrompt = promptBuilder.buildUserPrompt(session, messages, level);

        LlmResponse response = llmClient.generate(systemPrompt, userPrompt);

        System.out.println("RAW COACH RESPONSE:\n" + response.content());

        CoachPolicyResult policyResult = coachPolicyService.apply(response.content());

        System.out.println("POLICY RESULT: filtered=" + policyResult.filtered()
                + ", reason=" + policyResult.reason());

        return new CoachReply(
                policyResult.content(),
                response.model(),
                response.promptVersion(),
                policyResult.filtered(),
                policyResult.reason(),
                level
        );
    }

    private String extractLatestStudentMessage(List<Message> messages) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message message = messages.get(i);
            if (message.getRole() == MessageRole.STUDENT) {
                return message.getContent();
            }
        }
        return "";
    }

    public record CoachReply(
            String content,
            String llmModel,
            String promptVersion,
            boolean filtered,
            String filterReason,
            CoachResponseLevel coachResponseLevel
    ) {
    }
}