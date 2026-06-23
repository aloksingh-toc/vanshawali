package in.ibrahimabad.vanshawali.fund.dto;

import java.math.BigDecimal;
import java.util.List;

public record FundSummaryDto(
        BigDecimal totalContributions,
        BigDecimal totalExpenses,
        BigDecimal balance,
        List<FundEventBreakdownDto> byEvent) {
}
