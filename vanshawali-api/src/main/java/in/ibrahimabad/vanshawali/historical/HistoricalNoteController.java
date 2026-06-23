package in.ibrahimabad.vanshawali.historical;

import in.ibrahimabad.vanshawali.historical.dto.HistoricalNoteDto;
import in.ibrahimabad.vanshawali.historical.dto.HistoricalNoteWriteRequest;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Historical notes are admin-only to create/manage (enforced in SecurityConfig); surfaced publicly via /api/on-this-day. */
@RestController
@RequestMapping("/api/historical-notes")
public class HistoricalNoteController {

    private final HistoricalNoteService service;

    public HistoricalNoteController(HistoricalNoteService service) {
        this.service = service;
    }

    @GetMapping
    public List<HistoricalNoteDto> listAll() {
        return service.listAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HistoricalNoteDto create(@Valid @RequestBody HistoricalNoteWriteRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public HistoricalNoteDto update(@PathVariable Long id, @Valid @RequestBody HistoricalNoteWriteRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
