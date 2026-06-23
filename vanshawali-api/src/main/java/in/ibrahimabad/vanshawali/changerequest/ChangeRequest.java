package in.ibrahimabad.vanshawali.changerequest;

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
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "change_requests")
@Getter
@Setter
public class ChangeRequest implements Moderatable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_person_id")
    private Person targetPerson;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false, length = 20)
    private RequestType requestType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "proposed_data", columnDefinition = "jsonb")
    private Map<String, Object> proposedData;

    @Column(name = "requester_name", nullable = false)
    private String requesterName;

    @Column(name = "requester_contact")
    private String requesterContact;

    @Embedded
    private ModerationStatus moderation = new ModerationStatus();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
