package in.ibrahimabad.vanshawali.event.dto;

import in.ibrahimabad.vanshawali.event.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CommunityEventWriteRequest(
        @NotBlank String title,
        String description,
        @NotNull LocalDate eventDate,
        @NotNull EventType eventType,
        String location) {
}
