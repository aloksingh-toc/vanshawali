package in.ibrahimabad.vanshawali.historical;

import in.ibrahimabad.vanshawali.common.crud.CrudServiceBase;
import in.ibrahimabad.vanshawali.historical.dto.HistoricalNoteDto;
import in.ibrahimabad.vanshawali.historical.dto.HistoricalNoteWriteRequest;
import in.ibrahimabad.vanshawali.person.Person;
import in.ibrahimabad.vanshawali.person.PersonRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class HistoricalNoteService extends CrudServiceBase<HistoricalNote, Long> {

    private final HistoricalNoteRepository repository;
    private final PersonRepository personRepository;

    public HistoricalNoteService(HistoricalNoteRepository repository, PersonRepository personRepository) {
        super(repository, "Historical note");
        this.repository = repository;
        this.personRepository = personRepository;
    }

    public List<HistoricalNoteDto> listAll() {
        return repository.findAllByOrderByNoteMonthAscNoteDayAsc().stream().map(this::toDto).toList();
    }

    public List<HistoricalNote> findForToday() {
        LocalDate today = LocalDate.now();
        return repository.findByNoteMonthAndNoteDay(today.getMonthValue(), today.getDayOfMonth());
    }

    @Transactional
    public HistoricalNoteDto create(HistoricalNoteWriteRequest req) {
        HistoricalNote note = new HistoricalNote();
        apply(note, req);
        return toDto(repository.save(note));
    }

    @Transactional
    public HistoricalNoteDto update(Long id, HistoricalNoteWriteRequest req) {
        HistoricalNote note = getOrThrow(id);
        apply(note, req);
        return toDto(repository.save(note));
    }

    @Transactional
    public void delete(Long id) {
        deleteOrThrow(id);
    }

    private void apply(HistoricalNote note, HistoricalNoteWriteRequest req) {
        note.setNoteMonth(req.noteMonth());
        note.setNoteDay(req.noteDay());
        note.setTitle(req.title());
        note.setDescription(req.description());
        note.setPhotoUrl(req.photoUrl());
        if (req.relatedPersonId() != null) {
            Person person = personRepository.findById(req.relatedPersonId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Person not found: " + req.relatedPersonId()));
            note.setRelatedPerson(person);
        } else {
            note.setRelatedPerson(null);
        }
    }

    HistoricalNoteDto toDto(HistoricalNote n) {
        Person rp = n.getRelatedPerson();
        return new HistoricalNoteDto(
                n.getId(),
                n.getNoteMonth(),
                n.getNoteDay(),
                n.getTitle(),
                n.getDescription(),
                rp != null ? rp.getId() : null,
                rp != null ? rp.getName() : null,
                n.getPhotoUrl(),
                n.getCreatedAt());
    }
}
