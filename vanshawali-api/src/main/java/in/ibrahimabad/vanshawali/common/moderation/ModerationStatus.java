package in.ibrahimabad.vanshawali.common.moderation;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/**
 * Shared submit -> pending -> admin review -> approve/reject shape, embedded into
 * change_requests, gallery_posts, gallery_comments, and announcements.
 */
@Embeddable
@Getter
@Setter
public class ModerationStatus {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Status status = Status.PENDING;

    @Column(name = "admin_notes")
    private String adminNotes;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    public void approve(String notes) {
        this.status = Status.APPROVED;
        this.adminNotes = notes;
        this.reviewedAt = Instant.now();
    }

    public void reject(String notes) {
        this.status = Status.REJECTED;
        this.adminNotes = notes;
        this.reviewedAt = Instant.now();
    }
}
