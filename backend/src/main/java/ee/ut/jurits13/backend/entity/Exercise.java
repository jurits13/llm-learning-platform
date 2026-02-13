package ee.ut.jurits13.backend.entity;

import jakarta.persistence.*;

@Entity
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer difficulty;

    public Exercise() {
    }

    public Exercise(String title, String description, Integer difficulty) {
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }
}
