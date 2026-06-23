package in.ibrahimabad.vanshawali.announcement;

import in.ibrahimabad.vanshawali.announcement.dto.AnnouncementDto;
import in.ibrahimabad.vanshawali.announcement.dto.AnnouncementSubmitRequest;
import in.ibrahimabad.vanshawali.common.moderation.ModerationServiceBase;
import in.ibrahimabad.vanshawali.common.moderation.Status;
import in.ibrahimabad.vanshawali.common.util.Sorting;
import in.ibrahimabad.vanshawali.person.Person;
import in.ibrahimabad.vanshawali.person.PersonRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class AnnouncementService extends ModerationServiceBase<Announcement, Long> {

    private final AnnouncementRepository repository;
    private final PersonRepository personRepository;

    public AnnouncementService(AnnouncementRepository repository, PersonRepository personRepository) {
        super(repository);
        this.repository = repository;
        this.personRepository = personRepository;
    }

    @Transactional
    public AnnouncementDto submit(AnnouncementSubmitRequest req) {
        Announcement announcement = new Announcement();
        announcement.setTitle(req.title());
        announcement.setDescription(req.description());
        announcement.setAnnouncementType(req.announcementType());
        announcement.setSubmitterName(req.submitterName());
        announcement.setSubmitterContact(req.submitterContact());
        if (req.personId() != null) {
            Person person = personRepository.findById(req.personId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Person not found: " + req.personId()));
            announcement.setPerson(person);
        }
        return toDto(repository.save(announcement));
    }

    public List<AnnouncementDto> listApprovedDtos() {
        return repository.findByModeration_StatusOrderByCreatedAtDesc(Status.APPROVED).stream()
                .map(this::toDto)
                .toList();
    }

    public List<AnnouncementDto> listPendingDtos() {
        return listPending().stream().map(this::toDto).toList();
    }

    public List<AnnouncementDto> listAllDtos() {
        return Sorting.byKeyDesc(repository.findAll(), Announcement::getCreatedAt).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public AnnouncementDto approveRequest(Long id, String notes) {
        return toDto(approve(id, notes));
    }

    @Transactional
    public AnnouncementDto rejectRequest(Long id, String notes) {
        return toDto(reject(id, notes));
    }

    private AnnouncementDto toDto(Announcement a) {
        Person p = a.getPerson();
        return new AnnouncementDto(
                a.getId(),
                p != null ? p.getId() : null,
                p != null ? p.getName() : null,
                a.getTitle(),
                a.getDescription(),
                a.getAnnouncementType().name(),
                a.getSubmitterName(),
                a.getSubmitterContact(),
                a.getModeration().getStatus().name(),
                a.getModeration().getAdminNotes(),
                a.getModeration().getReviewedAt(),
                a.getCreatedAt());
    }
}
