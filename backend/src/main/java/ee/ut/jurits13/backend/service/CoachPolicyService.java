package ee.ut.jurits13.backend.service;

import org.springframework.stereotype.Service;

@Service
public class CoachPolicyService {

    public CoachPolicyResult apply(String rawContent) {
        if (rawContent == null || rawContent.isBlank()) {
            return new CoachPolicyResult(fallback(), true, "empty_response");
        }

        String trimmed = rawContent.trim();
        String lower = trimmed.toLowerCase();

        boolean hasLongCodeBlock = containsLongCodeBlock(trimmed);
        boolean tooDirect = isTooDirect(lower);
        boolean hasNoQuestion = !trimmed.contains("?");

        if (hasLongCodeBlock) {
            return new CoachPolicyResult(fallback(), true, "long_code_block");
        }

        if (tooDirect) {
            return new CoachPolicyResult(fallback(), true, "too_direct");
        }

        if (hasNoQuestion) {
            return new CoachPolicyResult(fallback(), true, "no_guiding_question");
        }

        return new CoachPolicyResult(trimmed, false, "accepted");
    }

    private boolean isTooDirect(String lower) {
        return lower.contains("here is the solution")
                || lower.contains("the solution is")
                || lower.contains("replace it with")
                || lower.contains("use this code")
                || lower.contains("the correct code is")
                || lower.contains("copy this")
                || lower.contains("paste this")
                || lower.contains("all you need to do is")
                || lower.contains("the fix is")
                || lower.contains("fixed version")
                || lower.contains("here’s the code")
                || lower.contains("here is the code")
                || lower.contains("change it to");
    }

    private boolean containsLongCodeBlock(String text) {
        if (!text.contains("```")) {
            return false;
        }

        String[] parts = text.split("```");
        for (int i = 1; i < parts.length; i += 2) {
            String block = parts[i].trim();
            long lineCount = block.lines().count();
            if (lineCount >= 5) {
                return true;
            }
        }
        return false;
    }

    private String fallback() {
        return """
                Let’s slow down and inspect the problem step by step.

                What did you expect to happen, and what actually happened?

                One useful next move is to test one small part of the flow and compare expected vs actual behavior.

                What would you check first to confirm whether the button was found and whether the click handler is running?
                """;
    }
}