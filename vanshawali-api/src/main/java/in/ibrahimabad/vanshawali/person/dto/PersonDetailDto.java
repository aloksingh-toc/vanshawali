package in.ibrahimabad.vanshawali.person.dto;

import java.time.LocalDate;

/** Single-person detail shape returned by GET /api/persons/{id}. */
public record PersonDetailDto(
        Long id,
        String name,
        String aliasNote,
        boolean directLine,
        boolean issueless,
        boolean unconfirmed,
        boolean pending,
        LocalDate birthDate,
        LocalDate deathDate,
        boolean dateIsApproximate,
        String photoUrl,
        int generation,
        Long parentId,
        String parentName) {
}
