package in.ibrahimabad.vanshawali.announcement;

import in.ibrahimabad.vanshawali.common.moderation.Moderatable;
import in.ibrahimabad.vanshawali.common.moderation.ModerationStatus;
import in.ibrahimabad.vanshawali.person.Person;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "announcements")
@Getter
@Setter
public class Announcement implements Moderatable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "announcement_type", nullable = false, length = 20)
    private AnnouncementType announcementType;

    @Column(name = "submitter_name", nullable = false)
    private String submitterName;

    @Column(name = "submitter_contact")
    private String submitterContact;

    @Embedded
    private ModerationStatus moderation = new ModerationStatus();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
