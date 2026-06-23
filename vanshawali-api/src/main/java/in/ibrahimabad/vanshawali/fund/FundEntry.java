package in.ibrahimabad.vanshawali.fund;

import in.ibrahimabad.vanshawali.event.CommunityEvent;
import jakarta.persistence.Column;
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
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "fund_entries")
@Getter
@Setter
public class FundEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FundMode mode;

    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 15)
    private FundEntryType entryType = FundEntryType.CONTRIBUTION;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_event_id")
    private CommunityEvent relatedEvent;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
