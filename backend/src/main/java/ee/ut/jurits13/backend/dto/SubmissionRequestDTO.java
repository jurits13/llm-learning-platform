package ee.ut.jurits13.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SubmissionRequestDTO {

    @NotNull(message = "Exercise id is required")
    private Long exerciseId;

    @NotNull(message="User id is required")
    private Long userId;

    @NotBlank(message = "Answer is required")
    @Size(max = 20_000, message = "Answer must be at most 20_000 characters")
    private String answer;

    public Long getExerciseId() {
        return exerciseId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
