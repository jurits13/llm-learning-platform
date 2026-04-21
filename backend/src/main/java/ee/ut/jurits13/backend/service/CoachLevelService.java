package ee.ut.jurits13.backend.service;

import ee.ut.jurits13.backend.entity.CoachResponseLevel;
import ee.ut.jurits13.backend.entity.Message;
import ee.ut.jurits13.backend.entity.MessageRole;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoachLevelService {

    public CoachResponseLevel determineLevel(List<Message> messages, String latestStudentMessage) {
        long studentCount = messages.stream()
                .filter(m -> m.getRole() == MessageRole.STUDENT)
                .count();

        String lower = normalize(latestStudentMessage);
        StudentProgress progress = assessProgress(latestStudentMessage);

        if (progress == StudentProgress.DIRECT_BEGINNER_SUPPORT) {
            return CoachResponseLevel.LEVEL_4_PARTIAL_EXPLANATION;
        }

        if (progress == StudentProgress.NEEDS_MORE_EXPLICIT_SUPPORT) {
            return CoachResponseLevel.LEVEL_4_PARTIAL_EXPLANATION;
        }

        if (isConceptQuestion(lower)) {
            return CoachResponseLevel.LEVEL_4_PARTIAL_EXPLANATION;
        }

        if (progress == StudentProgress.PRODUCED_WORKABLE_STEP
                || progress == StudentProgress.DEMONSTRATES_CORE_UNDERSTANDING) {
            return CoachResponseLevel.LEVEL_5_STRONG_SCAFFOLDING;
        }

        if (studentCount <= 1) {
            return CoachResponseLevel.LEVEL_1_REFLECTION;
        }

        if (studentCount == 2) {
            return CoachResponseLevel.LEVEL_2_HINT_AND_QUESTION;
        }

        if (studentCount == 3) {
            return CoachResponseLevel.LEVEL_3_DEBUG_GUIDE;
        }

        return CoachResponseLevel.LEVEL_5_STRONG_SCAFFOLDING;
    }

    public StudentProgress assessProgress(String latestStudentMessage) {
        String lower = normalize(latestStudentMessage);

        if (lower.isBlank()) {
            return StudentProgress.STILL_CONFUSED;
        }

        if (isDirectBeginnerSupportSignal(lower)) {
            return StudentProgress.DIRECT_BEGINNER_SUPPORT;
        }

        if (isAccidentalOrLowSignal(lower)) {
            return StudentProgress.LOW_SIGNAL;
        }

        if (needsMoreExplicitSupport(lower)) {
            return StudentProgress.NEEDS_MORE_EXPLICIT_SUPPORT;
        }

        if (producesWorkableStep(lower)) {
            return StudentProgress.PRODUCED_WORKABLE_STEP;
        }

        if (demonstratesUnderstanding(lower)) {
            return StudentProgress.DEMONSTRATES_CORE_UNDERSTANDING;
        }

        if (showsPartialUnderstanding(lower)) {
            return StudentProgress.PARTIAL_UNDERSTANDING;
        }

        return StudentProgress.STILL_CONFUSED;
    }

    private boolean isConceptQuestion(String lower) {
        return lower.contains("what is")
                || lower.contains("what does")
                || lower.contains("how does")
                || lower.contains("why does")
                || lower.contains("explain")
                || lower.contains("difference between");
    }

    private boolean demonstratesUnderstanding(String lower) {
        return containsAny(lower,
                "i understand",
                "that makes sense",
                "so the idea is",
                "so basically",
                "that means",
                "the problem is",
                "the issue is",
                "it returns",
                "it does not return",
                "it doesn't return",
                "it modifies",
                "it changes",
                "it reuses",
                "it creates",
                "it uses the same",
                "it stores",
                "it prints",
                "it will be",
                "it would be",
                "because",
                "so if"
        );
    }

    private boolean showsPartialUnderstanding(String lower) {
        return containsAny(lower,
                "i think",
                "i guess",
                "maybe",
                "i would assume",
                "i expected",
                "probably",
                "perhaps"
        );
    }

    private boolean producesWorkableStep(String lower) {
        return looksLikeCode(lower)
                || containsAny(lower,
                "i would change",
                "i would use",
                "we can use",
                "i can use",
                "maybe use",
                "return ",
                "print(",
                "if ",
                "for ",
                "while ",
                "=",
                ".append(",
                ".strip(",
                ".get(",
                "await ",
                "def ",
                "const ",
                "let "
        );
    }

    private boolean needsMoreExplicitSupport(String lower) {
        return containsAny(lower,
                "i don't understand",
                "i dont understand",
                "still don't understand",
                "still dont understand",
                "i am confused",
                "i'm confused",
                "not sure",
                "sorry",
                "i do not understand",
                "that is confusing",
                "what do you mean"
        );
    }

    private boolean isDirectBeginnerSupportSignal(String lower) {
        return containsAny(lower,
                "i am programming for the first time",
                "i'm programming for the first time",
                "i started programming only recently",
                "i only recently started programming",
                "i just started programming",
                "i am new to programming",
                "i'm new to programming",
                "im new to programming",
                "i am a beginner",
                "i'm a beginner",
                "im a beginner",
                "i know nothing",
                "i dont know anything",
                "i don't know anything",
                "i dont know syntax",
                "i don't know syntax",
                "i dont know what a method is",
                "i don't know what a method is",
                "what is a method",
                "what is a string operation",
                "what is string operation",
                "i give up",
                "i will never program again"
        );
    }

    private boolean isAccidentalOrLowSignal(String lower) {
        String trimmed = lower.trim();
        return trimmed.length() < 4
                || trimmed.matches("^[a-z]{4,}$")
                || trimmed.matches("^[^\\p{L}\\p{N}]+$");
    }

    private boolean looksLikeCode(String text) {
        return text.contains("(")
                || text.contains(")")
                || text.contains("{")
                || text.contains("}")
                || text.contains("[")
                || text.contains("]")
                || text.contains("=")
                || text.contains(":")
                || text.contains(".");
    }

    private boolean containsAny(String text, String... patterns) {
        for (String pattern : patterns) {
            if (text.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String text) {
        return text == null ? "" : text.toLowerCase().trim();
    }

    public enum StudentProgress {
        STILL_CONFUSED,
        PARTIAL_UNDERSTANDING,
        DEMONSTRATES_CORE_UNDERSTANDING,
        PRODUCED_WORKABLE_STEP,
        NEEDS_MORE_EXPLICIT_SUPPORT,
        DIRECT_BEGINNER_SUPPORT,
        LOW_SIGNAL
    }
}