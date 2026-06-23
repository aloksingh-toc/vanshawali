package in.ibrahimabad.vanshawali.gallery;

import in.ibrahimabad.vanshawali.common.moderation.Moderatable;
import in.ibrahimabad.vanshawali.common.moderation.ModerationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "gallery_comments")
@Getter
@Setter
public class GalleryComment implements Moderatable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private GalleryPost post;

    @Column(name = "commenter_name", nullable = false)
    private String commenterName;

    @Column(name = "body", nullable = false)
    private String body;

    @Embedded
    private ModerationStatus moderation = new ModerationStatus();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
