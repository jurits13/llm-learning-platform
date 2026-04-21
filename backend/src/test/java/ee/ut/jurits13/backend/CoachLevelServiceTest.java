package ee.ut.jurits13.backend;

import ee.ut.jurits13.backend.entity.CoachResponseLevel;
import ee.ut.jurits13.backend.entity.HelpSession;
import ee.ut.jurits13.backend.entity.Message;
import ee.ut.jurits13.backend.entity.MessageRole;
import ee.ut.jurits13.backend.service.CoachLevelService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
class CoachLevelServiceTest {

    private final CoachLevelService coachLevelService = new CoachLevelService();

    @Test
    void firstStudentTurn_returnsLevel1Reflection() {
        List<Message> messages = List.of(
                studentMessage("I added an event listener but nothing happens.")
        );

        CoachResponseLevel level = coachLevelService.determineLevel(
                messages,
                "I added an event listener but nothing happens."
        );

        assertEquals(CoachResponseLevel.LEVEL_1_REFLECTION, level);
    }

    @Test
    void secondStudentTurn_returnsLevel2HintAndQuestion() {
        List<Message> messages = List.of(
                studentMessage("My button does nothing."),
                coachMessage("What did you expect to happen?"),
                studentMessage("I checked and btn is null.")
        );

        CoachResponseLevel level = coachLevelService.determineLevel(
                messages,
                "I checked and btn is null."
        );

        assertEquals(CoachResponseLevel.LEVEL_2_HINT_AND_QUESTION, level);
    }

    @Test
    void thirdStudentTurn_returnsLevel3DebugGuide() {
        List<Message> messages = List.of(
                studentMessage("My button does nothing."),
                coachMessage("What did you expect to happen?"),
                studentMessage("I checked and btn is null."),
                coachMessage("What does null suggest here?"),
                studentMessage("I still do not understand why.")
        );

        CoachResponseLevel level = coachLevelService.determineLevel(
                messages,
                "I still do not understand why."
        );

        assertEquals(CoachResponseLevel.LEVEL_3_DEBUG_GUIDE, level);
    }

    @Test
    void conceptualQuestion_returnsLevel4PartialExplanation() {
        List<Message> messages = List.of(
                studentMessage("What does querySelector actually do?")
        );

        CoachResponseLevel level = coachLevelService.determineLevel(
                messages,
                "What does querySelector actually do?"
        );

        assertEquals(CoachResponseLevel.LEVEL_4_PARTIAL_EXPLANATION, level);
    }

    @Test
    void manyStudentTurns_returnsLevel5StrongScaffolding() {
        List<Message> messages = List.of(
                studentMessage("Problem 1"),
                coachMessage("Coach 1"),
                studentMessage("Problem 2"),
                coachMessage("Coach 2"),
                studentMessage("Problem 3"),
                coachMessage("Coach 3"),
                studentMessage("I am still stuck")
        );

        CoachResponseLevel level = coachLevelService.determineLevel(
                messages,
                "I am still stuck"
        );

        assertEquals(CoachResponseLevel.LEVEL_5_STRONG_SCAFFOLDING, level);
    }

    @Test
    void assessProgress_detectsCoreUnderstanding() {
        CoachLevelService.StudentProgress progress = coachLevelService.assessProgress(
                "I think the problem is that it returns a new value and does not modify the original one."
        );

        assertEquals(CoachLevelService.StudentProgress.DEMONSTRATES_CORE_UNDERSTANDING, progress);
    }

    @Test
    void assessProgress_detectsPartialUnderstanding() {
        CoachLevelService.StudentProgress progress = coachLevelService.assessProgress(
                "I think maybe it is doing something with the same value, but I am not fully sure yet."
        );

        assertEquals(CoachLevelService.StudentProgress.PARTIAL_UNDERSTANDING, progress);
    }

    @Test
    void assessProgress_detectsLowSignal() {
        CoachLevelService.StudentProgress progress = coachLevelService.assessProgress("asdsdaf");

        assertEquals(CoachLevelService.StudentProgress.LOW_SIGNAL, progress);
    }

    @Test
    void assessProgress_detectsNeedForMoreExplicitSupport() {
        CoachLevelService.StudentProgress progress = coachLevelService.assessProgress(
                "I dont understand, sorry"
        );

        assertEquals(CoachLevelService.StudentProgress.NEEDS_MORE_EXPLICIT_SUPPORT, progress);
    }

    @Test
    void determineLevel_whenStudentNeedsMoreExplicitSupport_returnsLevel4() {
        List<Message> messages = List.of(
                studentMessage("Why is new_items None?"),
                coachMessage("What do you think append returns?"),
                studentMessage("I dont understand, sorry")
        );

        CoachResponseLevel level = coachLevelService.determineLevel(
                messages,
                "I dont understand, sorry"
        );

        assertEquals(CoachResponseLevel.LEVEL_4_PARTIAL_EXPLANATION, level);
    }

    @Test
    void assessProgress_detectsDirectBeginnerSupport() {
        CoachLevelService.StudentProgress progress = coachLevelService.assessProgress(
                "I am programming for the first time and I know nothing"
        );

        assertEquals(CoachLevelService.StudentProgress.DIRECT_BEGINNER_SUPPORT, progress);
    }

    @Test
    void determineLevel_whenStudentNeedsDirectBeginnerSupport_returnsLevel4() {
        List<Message> messages = List.of(
                studentMessage("How can i remove these spaces"),
                coachMessage("What do you expect the output to look like?"),
                studentMessage("i am programming for the first time")
        );

        CoachResponseLevel level = coachLevelService.determineLevel(
                messages,
                "i am programming for the first time"
        );

        assertEquals(CoachResponseLevel.LEVEL_4_PARTIAL_EXPLANATION, level);
    }

    private Message studentMessage(String content) {
        return new Message((HelpSession) null, MessageRole.STUDENT, content);
    }

    private Message coachMessage(String content) {
        return new Message((HelpSession) null, MessageRole.COACH, content);
    }
}