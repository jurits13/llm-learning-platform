package ee.ut.jurits13.backend.llm;

import ee.ut.jurits13.backend.entity.HelpSession;
import ee.ut.jurits13.backend.entity.Message;
import ee.ut.jurits13.backend.entity.MessageRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PromptBuilder {

    private static final int MAX_HISTORY_MESSAGES = 6;

    public String buildSystemPrompt() {
        return """
                You are a programming learning coach helping a student learn web development and programming concepts.

                Your goal is to support understanding, not to provide ready-made solutions.

                Rules:
                - Do not provide full solutions or complete code unless explicitly asked by the system administrator.
                - Do not write the final answer for the student.
                - Give short hints and guiding questions instead of solving the task directly.
                - Encourage the student to reason step by step.
                - Encourage debugging, testing, and explanation in the student's own words.
                - If the student is stuck, break the problem into smaller parts.
                - If code is provided, focus on helping the student inspect and understand it.
                - If the student asks a conceptual question, explain briefly but still encourage active thinking.
                - Be supportive, concise, and clear.

                Preferred response structure:
                1. Brief acknowledgement
                2. One or two observations
                3. One hint
                4. One or two guiding questions
                5. One suggested next step

                Never shame the student.
                Never claim to have executed code.
                """;
    }

    public String buildUserPrompt(HelpSession session, List<Message> messages) {
        String latestStudentMessage = extractLatestStudentMessage(messages);
        String conversationHistory = buildConversationHistory(messages);

        return """
                Help session context

                Title:
                %s

                Problem description:
                %s

                Code snippet:
                %s

                What the student has already tried:
                %s

                Recent conversation:
                %s

                Latest student message:
                %s

                Respond as a learning coach. Do not provide a full solution.
                """.formatted(
                safe(session.getTitle()),
                safe(session.getProblemDescription()),
                safeBlock(session.getCodeSnippet()),
                safe(session.getWhatTried()),
                conversationHistory,
                safe(latestStudentMessage)
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

    private String buildConversationHistory(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return "No previous conversation.";
        }

        int start = Math.max(0, messages.size() - MAX_HISTORY_MESSAGES);

        return messages.subList(start, messages.size()).stream()
                .map(this::formatMessage)
                .collect(Collectors.joining("\n"));
    }

    private String formatMessage(Message message) {
        return "%s: %s".formatted(
                message.getRole().name(),
                safe(message.getContent())
        );
    }

    private String safe(String value) {
        if (value == null || value.isBlank()) {
            return "Not provided.";
        }
        return value.trim();
    }

    private String safeBlock(String value) {
        if (value == null || value.isBlank()) {
            return "No code snippet provided.";
        }
        return value.trim();
    }
}
