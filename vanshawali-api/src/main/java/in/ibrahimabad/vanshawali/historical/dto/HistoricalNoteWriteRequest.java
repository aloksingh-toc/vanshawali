package in.ibrahimabad.vanshawali.historical.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record HistoricalNoteWriteRequest(
        @Min(1) @Max(12) int noteMonth,
        @Min(1) @Max(31) int noteDay,
        @NotBlank String title,
        String description,
        Long relatedPersonId,
        String photoUrl) {
}
