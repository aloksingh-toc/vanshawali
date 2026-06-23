package in.ibrahimabad.vanshawali.historical.dto;

import java.time.Instant;

public record HistoricalNoteDto(
        Long id,
        int noteMonth,
        int noteDay,
        String title,
        String description,
        Long relatedPersonId,
        String relatedPersonName,
        String photoUrl,
        Instant createdAt) {
}
