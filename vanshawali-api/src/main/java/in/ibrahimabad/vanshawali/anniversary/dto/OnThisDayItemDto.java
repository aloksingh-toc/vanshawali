package in.ibrahimabad.vanshawali.anniversary.dto;

public record OnThisDayItemDto(
        String type,
        String title,
        String description,
        Long personId,
        String personName,
        String photoUrl,
        Integer yearsAgo) {
}
