package in.ibrahimabad.vanshawali.person.dto;

import java.util.List;

/** Nested tree shape returned by GET /api/tree. */
public record PersonNodeDto(
        Long id,
        String name,
        String aliasNote,
        boolean directLine,
        boolean issueless,
        boolean unconfirmed,
        boolean pending,
        String photoUrl,
        List<PersonNodeDto> children) {
}
