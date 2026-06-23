package in.ibrahimabad.vanshawali.gallery.dto;

import java.time.Instant;

public record GalleryPostDto(
        Long id,
        String photoUrl,
        String caption,
        String uploaderName,
        String uploaderContact,
        String status,
        String adminNotes,
        Instant reviewedAt,
        Instant createdAt,
        long approvedCommentCount) {
}
