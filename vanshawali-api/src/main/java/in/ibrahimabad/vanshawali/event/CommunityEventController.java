package in.ibrahimabad.vanshawali.event;

import in.ibrahimabad.vanshawali.event.dto.CommunityEventDto;
import in.ibrahimabad.vanshawali.event.dto.CommunityEventWriteRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Reading the calendar is public; create/edit/delete is admin-only (enforced in SecurityConfig). */
@RestController
@RequestMapping("/api/events")
public class CommunityEventController {

    private final CommunityEventService service;

    public CommunityEventController(CommunityEventService service) {
        this.service = service;
    }

    @GetMapping
    public List<CommunityEventDto> list(@RequestParam(required = false) EventType type) {
        return service.list(type);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommunityEventDto create(@Valid @RequestBody CommunityEventWriteRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public CommunityEventDto update(@PathVariable Long id, @Valid @RequestBody CommunityEventWriteRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
