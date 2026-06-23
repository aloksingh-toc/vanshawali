package in.ibrahimabad.vanshawali.security.dto;

public record LoginResponse(String token, String username, String displayName, String role) {
}
