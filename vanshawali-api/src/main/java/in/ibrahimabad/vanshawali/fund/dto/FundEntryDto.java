package in.ibrahimabad.vanshawali.fund.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record FundEntryDto(
        Long id,
        String name,
        BigDecimal amount,
        LocalDate entryDate,
        String mode,
        String note,
        String entryType,
        Long relatedEventId,
        String relatedEventTitle,
        String receiptUrl,
        Instant createdAt) {
}
