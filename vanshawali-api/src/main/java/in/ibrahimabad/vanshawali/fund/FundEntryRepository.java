package in.ibrahimabad.vanshawali.fund;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundEntryRepository extends JpaRepository<FundEntry, Long> {

    List<FundEntry> findAllByOrderByEntryDateDescCreatedAtDesc();

    List<FundEntry> findByEntryTypeOrderByEntryDateDescCreatedAtDesc(FundEntryType entryType);

    List<FundEntry> findByRelatedEvent_IdOrderByEntryDateDescCreatedAtDesc(Long relatedEventId);

    List<FundEntry> findByEntryTypeAndRelatedEvent_IdOrderByEntryDateDescCreatedAtDesc(
            FundEntryType entryType, Long relatedEventId);
}
