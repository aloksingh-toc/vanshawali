package in.ibrahimabad.vanshawali.fund.dto;

import in.ibrahimabad.vanshawali.fund.FundEntryType;
import in.ibrahimabad.vanshawali.fund.FundMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record FundEntryWriteRequest(
        @NotBlank String name,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotNull LocalDate entryDate,
        @NotNull FundMode mode,
        String note,
        @NotNull FundEntryType entryType,
        Long relatedEventId,
        String receiptUrl) {
}
