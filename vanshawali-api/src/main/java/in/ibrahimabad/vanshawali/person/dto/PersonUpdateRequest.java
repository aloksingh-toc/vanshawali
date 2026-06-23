package in.ibrahimabad.vanshawali.person.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record PersonUpdateRequest(
        Long parentId,
        @NotBlank String name,
        String aliasNote,
        boolean directLine,
        boolean issueless,
        boolean unconfirmed,
        boolean pending,
        LocalDate birthDate,
        LocalDate deathDate,
        boolean dateIsApproximate,
        String photoUrl) {
}
