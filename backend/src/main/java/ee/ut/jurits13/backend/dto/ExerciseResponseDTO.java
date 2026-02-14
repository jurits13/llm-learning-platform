package ee.ut.jurits13.backend.dto;

public class ExerciseResponseDTO {

    private Long id;
    private String title;
    private String description;
    private Integer difficulty;

    public ExerciseResponseDTO(Long id, String title, String description, Integer difficulty) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
    }

    public Long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public Integer getDifficulty() {
        return difficulty;
    }
}
