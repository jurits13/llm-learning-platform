package ee.ut.jurits13.backend.dto;

import java.time.Instant;

public class HelpSessionResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private String title;
    private String problemDescription;
    private String codeSnippet;
    private String whatTried;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    public HelpSessionResponseDTO(
            Long id,
            Long userId,
            String username,
            String title,
            String problemDescription,
            String codeSnippet,
            String whatTried,
            String status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.title = title;
        this.problemDescription = problemDescription;
        this.codeSnippet = codeSnippet;
        this.whatTried = whatTried;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public String getCodeSnippet() {
        return codeSnippet;
    }

    public String getWhatTried() {
        return whatTried;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
