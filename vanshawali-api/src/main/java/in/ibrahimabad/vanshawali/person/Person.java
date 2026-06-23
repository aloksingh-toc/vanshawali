package in.ibrahimabad.vanshawali.person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "persons")
@Getter
@Setter
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Person parent;

    @Column(nullable = false)
    private String name;

    @Column(name = "alias_note")
    private String aliasNote;

    @Column(name = "is_direct_line", nullable = false)
    private boolean directLine;

    @Column(name = "is_issueless", nullable = false)
    private boolean issueless;

    @Column(name = "is_unconfirmed", nullable = false)
    private boolean unconfirmed;

    @Column(name = "is_pending", nullable = false)
    private boolean pending;

    @Column(name = "sibling_order", nullable = false)
    private int siblingOrder;

    @Column(nullable = false)
    private int generation;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "death_date")
    private LocalDate deathDate;

    @Column(name = "date_is_approximate", nullable = false)
    private boolean dateIsApproximate;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "sheet_name")
    private String sheetName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
