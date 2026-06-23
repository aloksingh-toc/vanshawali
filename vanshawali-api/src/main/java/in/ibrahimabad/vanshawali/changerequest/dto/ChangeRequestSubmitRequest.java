package in.ibrahimabad.vanshawali.changerequest.dto;

import in.ibrahimabad.vanshawali.changerequest.RequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record ChangeRequestSubmitRequest(
        Long targetPersonId,
        @NotNull RequestType requestType,
        Map<String, Object> proposedData,
        @NotBlank String requesterName,
        String requesterContact) {
}
