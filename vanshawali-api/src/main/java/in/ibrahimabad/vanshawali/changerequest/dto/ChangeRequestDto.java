package in.ibrahimabad.vanshawali.changerequest.dto;

import in.ibrahimabad.vanshawali.changerequest.RequestType;
import java.time.Instant;
import java.util.Map;

/** Response shape for both the public submit endpoint and the admin inbox list. */
public record ChangeRequestDto(
        Long id,
        Long targetPersonId,
        String targetPersonName,
        RequestType requestType,
        Map<String, Object> proposedData,
        String requesterName,
        String requesterContact,
        String status,
        String adminNotes,
        Instant reviewedAt,
        Instant createdAt) {
}
