package in.ibrahimabad.vanshawali.user.dto;

import java.time.Instant;

public record AppUserDto(Long id, String username, String displayName, String role, Instant createdAt) {
}
