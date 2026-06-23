package in.ibrahimabad.vanshawali.user.dto;

import in.ibrahimabad.vanshawali.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AppUserCreateRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 6) String password,
        String displayName,
        @NotNull Role role) {
}
