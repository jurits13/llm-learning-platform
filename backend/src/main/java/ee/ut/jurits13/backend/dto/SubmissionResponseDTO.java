package ee.ut.jurits13.backend.dto;

import java.time.Instant;

public class SubmissionResponseDTO {

    private Long id;
    private Long exerciseId;

    private Long userId;
    private String username;

    private String answer;
    private Boolean isCorrect;
    private String feedback;
    private Instant createdAt;
    private String llmModel;
    private String promptVersion;
    private Instant evaluatedAt;


    public SubmissionResponseDTO(
            Long id,
            Long exerciseId,
            Long userId,
            String username,
            String answer,
            Boolean isCorrect,
            String feedback,
            Instant createdAt,
            String llmModel,
            String promptVersion,
            Instant evaluatedAt
    ) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.userId = userId;
        this.username = username;
        this.answer = answer;
        this.isCorrect = isCorrect;
        this.feedback = feedback;
        this.createdAt = createdAt;
        this.llmModel = llmModel;
        this.promptVersion = promptVersion;
        this.evaluatedAt = evaluatedAt;
    }

    public Long getId() { return id; }
    public Long getExerciseId() { return exerciseId; }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }

    public String getAnswer() { return answer; }
    public Boolean getIsCorrect() { return isCorrect; }
    public String getFeedback() { return feedback; }
    public Instant getCreatedAt() { return createdAt; }

    public String getLlmModel() {
        return llmModel;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public Instant getEvaluatedAt() {
        return evaluatedAt;
    }
}
