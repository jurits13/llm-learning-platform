package ee.ut.jurits13.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Exercise exercise;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Lob
    @Column(nullable = false)
    private String answer;

    // null = not evaluated yet, true/false = evaluated
    private Boolean isCorrect;

    @Column(length = 4000)
    private String feedback;

    @NotNull
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(length = 100)
    private String llmModel;

    @Column(length = 50)
    private String promptVersion;

    @Column
    private Instant evaluatedAt;

    protected Submission() {
    }

    public Submission(Exercise exercise, User user, String answer) {
        this.exercise = exercise;
        this.user = user;
        this.answer = answer;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public User getUser() {
        return user;
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

    public String getLlmModel() {
        return llmModel;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public Instant getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setLlmModel(String llmModel) {
        this.llmModel = llmModel;
    }

    public void setPromptVersion(String promptVersion) {
        this.promptVersion = promptVersion;
    }

    public void setEvaluatedAt(Instant evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

}
