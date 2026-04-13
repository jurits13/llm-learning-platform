package ee.ut.jurits13.backend.service;

import ee.ut.jurits13.backend.entity.HelpSession;
import ee.ut.jurits13.backend.entity.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoachService {
    public CoachReply generateReply(HelpSession session, List<Message> messages) {
        String latestStudentMessage = messages.isEmpty()
                ? ""
                : messages.get(messages.size() - 1).getContent();

        String feedback = """
                I will help you as a learning coach.

                What to think about next:
                1) What exactly is the expected behavior?
                2) Which part of your current solution are you least sure about?
                3) Can you test one smaller part of the problem separately?

                Hint:
                - Try to explain the issue step by step in your own words before changing the code.

                Based on your latest message:
                "%s"
                """.formatted(latestStudentMessage);

        return new CoachReply(feedback, "stub", "v1");
    }

    public record CoachReply(String content, String llmModel, String promptVersion) {
    }
}
