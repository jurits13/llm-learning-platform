package ee.ut.jurits13.backend.llm;

import ee.ut.jurits13.backend.entity.HelpSession;
import ee.ut.jurits13.backend.entity.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptBuilder {
    public String buildSystemPrompt() {
        return """
        You are a programming learning coach.

        Rules:
        - Do NOT give full solutions
        - Give hints
        - Ask guiding questions
        - Encourage reasoning
        """;
    }

    public String buildUserPrompt(HelpSession session, List<Message> messages) {
        String latestMessage = messages.isEmpty()
                ? ""
                : messages.getLast().getContent();

        return """
                Problem:
                %s

                Code:
                %s

                What tried:
                %s

                Latest message:
                %s
                """.formatted(
                session.getProblemDescription(),
                session.getCodeSnippet(),
                session.getWhatTried(),
                latestMessage
        );
    }
}
