package in.ibrahimabad.vanshawali.event;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityEventRepository extends JpaRepository<CommunityEvent, Long> {

    List<CommunityEvent> findAllByOrderByEventDateAsc();

    List<CommunityEvent> findByEventTypeOrderByEventDateAsc(EventType eventType);
}
