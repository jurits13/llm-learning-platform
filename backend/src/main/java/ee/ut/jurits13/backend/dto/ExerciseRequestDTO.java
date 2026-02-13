package ee.ut.jurits13.backend.dto;

import jakarta.validation.constraints.*;

public class ExerciseRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must be at most 2000 characters")
    private String description;

    @NotNull(message = "Difficulty is required")
    @Min(value = 1, message = "Difficulty must be at least 1")
    @Max(value = 5, message = "Difficulty must be at most 5")
    private Integer difficulty;

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Integer getDifficulty() { return difficulty; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
}
