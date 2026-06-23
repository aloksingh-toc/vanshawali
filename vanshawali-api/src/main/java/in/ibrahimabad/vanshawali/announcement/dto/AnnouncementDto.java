package in.ibrahimabad.vanshawali.announcement.dto;

import java.time.Instant;

public record AnnouncementDto(
        Long id,
        Long personId,
        String personName,
        String title,
        String description,
        String announcementType,
        String submitterName,
        String submitterContact,
        String status,
        String adminNotes,
        Instant reviewedAt,
        Instant createdAt) {
}
