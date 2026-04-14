package ee.ut.jurits13.backend.dto;

import java.time.Instant;

public class MessageResponseDTO {
    private Long id;
    private String role;
    private String content;
    private Instant createdAt;
    private String llmModel;
    private String promptVersion;
    private boolean filteredByPolicy;
    private String policyReason;

    public MessageResponseDTO(
            Long id,
            String role,
            String content,
            Instant createdAt,
            String llmModel,
            String promptVersion,
            boolean filteredByPolicy,
            String policyReason
    ) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
        this.llmModel = llmModel;
        this.promptVersion = promptVersion;
        this.filteredByPolicy = filteredByPolicy;
        this.policyReason = policyReason;
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
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

    public boolean isFilteredByPolicy() {
        return filteredByPolicy;
    }

    public String getPolicyReason() {
        return policyReason;
    }
}
