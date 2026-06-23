package in.ibrahimabad.vanshawali.gallery;

import in.ibrahimabad.vanshawali.common.moderation.Moderatable;
import in.ibrahimabad.vanshawali.common.moderation.ModerationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "gallery_posts")
@Getter
@Setter
public class GalleryPost implements Moderatable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "photo_url", nullable = false)
    private String photoUrl;

    @Column(name = "caption")
    private String caption;

    @Column(name = "uploader_name", nullable = false)
    private String uploaderName;

    @Column(name = "uploader_contact")
    private String uploaderContact;

    @Embedded
    private ModerationStatus moderation = new ModerationStatus();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
