package in.ibrahimabad.vanshawali.anniversary.dto;

import java.time.LocalDate;

public record AnniversaryEntryDto(
        Long personId,
        String personName,
        String type,
        LocalDate originalDate,
        boolean dateIsApproximate,
        int daysUntil,
        Integer yearsAtOccurrence) {
}
