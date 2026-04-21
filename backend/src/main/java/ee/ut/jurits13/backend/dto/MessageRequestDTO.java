package ee.ut.jurits13.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class MessageRequestDTO {
    @NotBlank
    @Size(max = 10000)
    @Pattern(
            regexp = "(?s).*[\\p{L}\\p{N}].*",
            message = "Content must include at least one letter or number"
    )
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
