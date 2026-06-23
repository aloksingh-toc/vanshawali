package in.ibrahimabad.vanshawali.fund.dto;

import java.math.BigDecimal;

public record FundEventBreakdownDto(
        Long eventId, String eventTitle, BigDecimal contributions, BigDecimal expenses, BigDecimal balance) {
}
