package ee.ut.jurits13.backend.service;

import ee.ut.jurits13.backend.entity.Exercise;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    public String generateCoachFeedback(Exercise exercise, String studentAnswer) {
        // Stub feedback for MVP: coach-style, no full solutions
        return """
        Coach feedback (stub)

        What to check:
        1) Can you explain in your own words what your current solution is doing step-by-step?
        2) Which part of the exercise requirements is already satisfied, and which part is missing?
        3) If your solution fails, what input case would reproduce the failure?

        Hint:
        - Re-read the exercise description and try to break the problem into smaller steps.
        - Add a simple print/log to verify intermediate values.

        Next step:
        - Make one small change and resubmit, then compare the result.
        """;
    }
}
