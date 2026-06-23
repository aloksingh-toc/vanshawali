package in.ibrahimabad.vanshawali.event.dto;

import java.time.Instant;
import java.time.LocalDate;

public record CommunityEventDto(
        Long id,
        String title,
        String description,
        LocalDate eventDate,
        String eventType,
        String location,
        Instant createdAt) {
}
