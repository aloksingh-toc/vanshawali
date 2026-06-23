package in.ibrahimabad.vanshawali.announcement.dto;

import in.ibrahimabad.vanshawali.announcement.AnnouncementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnnouncementSubmitRequest(
        Long personId,
        @NotBlank String title,
        String description,
        @NotNull AnnouncementType announcementType,
        @NotBlank String submitterName,
        String submitterContact) {
}
