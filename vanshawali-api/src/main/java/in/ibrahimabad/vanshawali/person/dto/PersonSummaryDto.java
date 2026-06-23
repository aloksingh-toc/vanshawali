package in.ibrahimabad.vanshawali.person.dto;

/** Flat summary shape used in GET /api/search results. */
public record PersonSummaryDto(
        Long id,
        String name,
        String aliasNote,
        boolean directLine,
        int generation,
        Long parentId,
        String parentName) {
}
