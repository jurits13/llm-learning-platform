package ee.ut.jurits13.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private HelpSession helpSession;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private MessageRole role;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(length = 100)
    private String llmModel;

    @Column(length = 50)
    private String promptVersion;

    @Column(nullable = false)
    private boolean filteredByPolicy = false;

    @Column(length = 100)
    private String policyReason;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    protected Message() {
    }

    public Message(HelpSession helpSession, MessageRole role, String content) {
        this.helpSession = helpSession;
        this.role = role;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public HelpSession getHelpSession() {
        return helpSession;
    }

    public void setHelpSession(HelpSession helpSession) {
        this.helpSession = helpSession;
    }

    public MessageRole getRole() {
        return role;
    }

    public void setRole(MessageRole role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getLlmModel() {
        return llmModel;
    }

    public void setLlmModel(String llmModel) {
        this.llmModel = llmModel;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public void setPromptVersion(String promptVersion) {
        this.promptVersion = promptVersion;
    }

    public boolean isFilteredByPolicy() {
        return filteredByPolicy;
    }

    public void setFilteredByPolicy(boolean filteredByPolicy) {
        this.filteredByPolicy = filteredByPolicy;
    }

    public String getPolicyReason() {
        return policyReason;
    }

    public void setPolicyReason(String policyReason) {
        this.policyReason = policyReason;
    }
}
