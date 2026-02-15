package ee.ut.jurits13.backend.dto;

import java.time.Instant;

public class SubmissionResponseDTO {

    private Long id;
    private Long exerciseId;
    //private User user;
    private String studentIdentifier;
    private String answer;
    private Boolean isCorrect;
    private String feedback;
    private Instant createdAt;

    public SubmissionResponseDTO(Long id, Long exerciseId, String studentIdentifier, String answer, Boolean isCorrect, String feedback, Instant createdAt) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.studentIdentifier = studentIdentifier;
        this.answer = answer;
        this.isCorrect = isCorrect;
        this.feedback = feedback;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public String getStudentIdentifier() {
        return studentIdentifier;
    }

    public String getAnswer() {
        return answer;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public String getFeedback() {
        return feedback;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
