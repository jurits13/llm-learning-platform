package ee.ut.jurits13.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class HelpSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(length = 4000, nullable = false)
    private String problemDescription;

    @Lob
    private String codeSnippet;

    @Column(length = 2000)
    private String whatTried;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private HelpSessionStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = HelpSessionStatus.OPEN;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    protected HelpSession() {
    }

    public HelpSession(User user, String title, String problemDescription, String codeSnippet, String whatTried) {
        this.user = user;
        this.title = title;
        this.problemDescription = problemDescription;
        this.codeSnippet = codeSnippet;
        this.whatTried = whatTried;
    }

    public Long getId() {
        return id;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public String getCodeSnippet() {
        return codeSnippet;
    }

    public void setCodeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }

    public String getWhatTried() {
        return whatTried;
    }

    public void setWhatTried(String whatTried) {
        this.whatTried = whatTried;
    }

    public HelpSessionStatus getStatus() {
        return status;
    }

    public void setStatus(HelpSessionStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
