package ee.ut.jurits13.backend.service;

public record CoachPolicyResult(
        String content,
        boolean filtered,
        String reason
) {
}