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

        String lower = latestStudentMessage == null ? "" : latestStudentMessage.toLowerCase();

        if (isConceptQuestion(lower)) {
            return CoachResponseLevel.LEVEL_4_PARTIAL_EXPLANATION;
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

    private boolean isConceptQuestion(String lower) {
        return lower.contains("what is")
                || lower.contains("what does")
                || lower.contains("how does")
                || lower.contains("why does")
                || lower.contains("explain")
                || lower.contains("difference between");
    }
}