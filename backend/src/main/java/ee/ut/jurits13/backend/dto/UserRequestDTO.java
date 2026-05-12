package ee.ut.jurits13.backend.dto;

import ee.ut.jurits13.backend.entity.UserRole;
import jakarta.validation.constraints.*;

public class UserRequestDTO {

    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username must be at most 100 characters")
    @Pattern(
            regexp = "(?s).*[\\p{L}\\p{N}].*",
            message = "Username must include at least one letter or number"
    )
    private String username;

    @NotNull(message = "User role is required")
    private UserRole role;

    public String getUsername() {
        return username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
