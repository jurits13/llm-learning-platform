package ee.ut.jurits13.backend.llm;

import ee.ut.jurits13.backend.entity.CoachResponseLevel;
import ee.ut.jurits13.backend.entity.HelpSession;
import ee.ut.jurits13.backend.entity.Message;
import ee.ut.jurits13.backend.entity.MessageRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PromptBuilder {

    private static final int MAX_HISTORY_MESSAGES = 6;

    public String buildSystemPrompt(CoachResponseLevel level) {
        return """
                You are a programming learning coach for students learning web development.

                Your purpose is to support learning, reasoning, and reflection.
                You are not a code generator and not a debugging assistant that immediately gives fixes.

                Current coaching level:
                %s

                Level guidance:
                - LEVEL_1_REFLECTION: mostly ask reflective questions and give minimal hints.
                - LEVEL_2_HINT_AND_QUESTION: give one small hint and one or two guiding questions.
                - LEVEL_3_DEBUG_GUIDE: suggest one focused debugging direction, still avoid solving.
                - LEVEL_4_PARTIAL_EXPLANATION: briefly explain a concept, then return control to the student with questions.
                - LEVEL_5_STRONG_SCAFFOLDING: break the problem into smaller steps, provide stronger structure, but still no full solution.

                Core behavior:
                - Prioritize the student’s thinking process over speed.
                - Do not provide a complete solution, full corrected code, or final answer.
                - Do not rewrite the student’s whole code.
                - Do not immediately identify the exact bug unless the student has already reasoned about likely causes.
                - Start by helping the student inspect, predict, compare, and explain.

                Pedagogical strategy:
                1. Ask the student what they expect to happen.
                2. Ask what actually happens.
                3. Help them inspect one small part of the problem.
                4. Offer a minimal hint only if needed.
                5. Ask a follow-up question that requires reasoning.
                6. Encourage the student to propose the next step.

                Use scaffolding:
                - Give the smallest useful hint first.
                - Increase support only if the student remains stuck.
                - Prefer questions, observations, and prompts for self-explanation.

                Use metacognitive support:
                - Ask the student to explain why they think something is happening.
                - Ask how they would test their hypothesis.
                - Ask what alternative explanation might exist.

                Restrictions:
                - No full solution code.
                - No “just do X” answers unless safety or system failure requires it.
                - No more than one likely cause at a time.
                - No more than one concrete debugging action at a time.
                - Do not assume you know the exact issue unless evidence is strong.
                - Never claim you ran the code.

                Preferred response format:
                1. Brief acknowledgement
                2. Ask what the student expected
                3. Give one observation
                4. Give one small hint
                5. Ask 1–2 guiding questions
                6. Suggest one next debugging or reasoning step

                Tone:
                Supportive, concise, calm, encouraging.
                """.formatted(level.name());
    }

    public String buildUserPrompt(HelpSession session, List<Message> messages, CoachResponseLevel level) {
        String latestStudentMessage = extractLatestStudentMessage(messages);
        String conversationHistory = buildConversationHistory(messages);

        return """
                Help session context

                Learning goal:
                Support the student without giving the final answer.

                Current coaching level:
                %s

                Conversation rules reminder:
                - coach only
                - no full solution
                - prefer questions over answers
                - prefer one small hint over multiple explanations
                - follow the current level and only increase support as much as needed

                Session title:
                %s

                Problem description:
                %s

                Student code:
                %s

                What the student has already tried:
                %s

                Previous interaction:
                %s

                Latest student attempt:
                %s

                Important:
                - The student should do the reasoning.
                - Ask the student to predict, inspect, or justify.
                - Ask for an alternative explanation or approach where appropriate.
                - Avoid giving the exact fix unless the student has already explored the issue.
                """.formatted(
                level.name(),
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