package in.ibrahimabad.vanshawali.gallery.dto;

import java.time.Instant;

public record GalleryCommentDto(
        Long id,
        Long postId,
        String commenterName,
        String body,
        String status,
        String adminNotes,
        Instant createdAt) {
}
