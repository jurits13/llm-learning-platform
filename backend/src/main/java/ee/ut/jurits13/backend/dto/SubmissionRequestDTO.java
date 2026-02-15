package ee.ut.jurits13.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SubmissionRequestDTO {

    @NotNull(message = "Exercise id is required")
    private Long exerciseId;

    @NotBlank(message = "Student identifier is required")
    @Size(max = 100, message = "Student identifier must be at most 100 characters")
    private String studentIdentifier;

    @NotBlank(message = "Answer is required")
    @Size(max = 20_000, message = "Answer must be at most 20_000 characters")
    private String answer;

    public Long getExerciseId() {
        return exerciseId;
    }

    public String getStudentIdentifier() {
        return studentIdentifier;
    }

    public String getAnswer() {
        return answer;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public void setStudentIdentifier(String studentIdentifier) {
        this.studentIdentifier = studentIdentifier;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
