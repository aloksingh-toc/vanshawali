package in.ibrahimabad.vanshawali.event;

import in.ibrahimabad.vanshawali.common.crud.CrudServiceBase;
import in.ibrahimabad.vanshawali.event.dto.CommunityEventDto;
import in.ibrahimabad.vanshawali.event.dto.CommunityEventWriteRequest;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CommunityEventService extends CrudServiceBase<CommunityEvent, Long> {

    private final CommunityEventRepository repository;

    public CommunityEventService(CommunityEventRepository repository) {
        super(repository, "Event");
        this.repository = repository;
    }

    public List<CommunityEventDto> list(EventType type) {
        List<CommunityEvent> events =
                type != null ? repository.findByEventTypeOrderByEventDateAsc(type) : repository.findAllByOrderByEventDateAsc();
        return events.stream().map(this::toDto).toList();
    }

    @Transactional
    public CommunityEventDto create(CommunityEventWriteRequest req) {
        CommunityEvent event = new CommunityEvent();
        apply(event, req);
        return toDto(repository.save(event));
    }

    @Transactional
    public CommunityEventDto update(Long id, CommunityEventWriteRequest req) {
        CommunityEvent event = getOrThrow(id);
        apply(event, req);
        return toDto(repository.save(event));
    }

    @Transactional
    public void delete(Long id) {
        deleteOrThrow(id);
    }

    private void apply(CommunityEvent event, CommunityEventWriteRequest req) {
        event.setTitle(req.title());
        event.setDescription(req.description());
        event.setEventDate(req.eventDate());
        event.setEventType(req.eventType());
        event.setLocation(req.location());
    }

    private CommunityEventDto toDto(CommunityEvent e) {
        return new CommunityEventDto(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getEventDate(),
                e.getEventType().name(),
                e.getLocation(),
                e.getCreatedAt());
    }
}
