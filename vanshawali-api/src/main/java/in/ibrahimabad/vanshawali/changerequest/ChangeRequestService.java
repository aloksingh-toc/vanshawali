package in.ibrahimabad.vanshawali.changerequest;

import in.ibrahimabad.vanshawali.changerequest.dto.ChangeRequestDto;
import in.ibrahimabad.vanshawali.changerequest.dto.ChangeRequestSubmitRequest;
import in.ibrahimabad.vanshawali.common.moderation.ModerationServiceBase;
import in.ibrahimabad.vanshawali.common.util.Sorting;
import in.ibrahimabad.vanshawali.person.Person;
import in.ibrahimabad.vanshawali.person.PersonRepository;
import in.ibrahimabad.vanshawali.person.PersonService;
import in.ibrahimabad.vanshawali.person.dto.PersonCreateRequest;
import in.ibrahimabad.vanshawali.person.dto.PersonDetailDto;
import in.ibrahimabad.vanshawali.person.dto.PersonFlagsRequest;
import in.ibrahimabad.vanshawali.person.dto.PersonUpdateRequest;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Submitting is public; approving applies the proposed change to the live {@link Person} tree
 * (reusing PersonService so generation/siblingOrder/audit stay consistent with direct admin edits).
 */
@Service
@Transactional(readOnly = true)
public class ChangeRequestService extends ModerationServiceBase<ChangeRequest, Long> {

    private final ChangeRequestRepository repository;
    private final PersonRepository personRepository;
    private final PersonService personService;

    public ChangeRequestService(
            ChangeRequestRepository repository, PersonRepository personRepository, PersonService personService) {
        super(repository);
        this.repository = repository;
        this.personRepository = personRepository;
        this.personService = personService;
    }

    @Transactional
    public ChangeRequestDto submit(ChangeRequestSubmitRequest req) {
        ChangeRequest entity = new ChangeRequest();
        if (req.targetPersonId() != null) {
            Person target = personRepository.findById(req.targetPersonId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Person not found: " + req.targetPersonId()));
            entity.setTargetPerson(target);
        }
        entity.setRequestType(req.requestType());
        entity.setProposedData(req.proposedData());
        entity.setRequesterName(req.requesterName());
        entity.setRequesterContact(req.requesterContact());

        return toDto(repository.save(entity));
    }

    public List<ChangeRequestDto> listPendingDtos() {
        return listPending().stream().map(this::toDto).toList();
    }

    public List<ChangeRequestDto> listAllDtos() {
        return Sorting.byKeyDesc(repository.findAll(), ChangeRequest::getCreatedAt).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ChangeRequestDto approveAndApply(Long id, String notes, String reviewedBy) {
        ChangeRequest cr = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Change request not found: " + id));

        applyChange(cr, reviewedBy);
        return toDto(approve(id, notes));
    }

    @Transactional
    public ChangeRequestDto rejectRequest(Long id, String notes) {
        return toDto(reject(id, notes));
    }

    private void applyChange(ChangeRequest cr, String reviewedBy) {
        Map<String, Object> data = cr.getProposedData() != null ? cr.getProposedData() : Map.of();
        Person target = cr.getTargetPerson();
        String changedBy = "request#" + cr.getId() + ":" + reviewedBy;

        switch (cr.getRequestType()) {
            case RENAME -> {
                Long targetId = requireTargetId(target, cr.getId());
                String newName = requireString(data, "name", cr.getId());
                PersonDetailDto d = personService.getById(targetId);
                personService.updatePerson(targetId, withName(d, newName), changedBy);
            }
            case ADD_CHILD -> {
                Long targetId = requireTargetId(target, cr.getId());
                String name = requireString(data, "name", cr.getId());
                Object aliasNote = data.get("aliasNote");
                PersonCreateRequest create = new PersonCreateRequest(
                        targetId,
                        name,
                        aliasNote instanceof String s ? s : null,
                        false,
                        false,
                        true,
                        true,
                        null,
                        null,
                        false,
                        null);
                personService.createPerson(create, changedBy);
            }
            case MARK_ISSUELESS -> {
                Long targetId = requireTargetId(target, cr.getId());
                personService.updateFlags(targetId, new PersonFlagsRequest(null, true, null, null), changedBy);
            }
            case CONFIRM_NAME -> {
                Long targetId = requireTargetId(target, cr.getId());
                personService.updateFlags(targetId, new PersonFlagsRequest(null, null, false, null), changedBy);
            }
            case ADD_NOTE -> {
                Long targetId = requireTargetId(target, cr.getId());
                String note = requireString(data, "note", cr.getId());
                PersonDetailDto d = personService.getById(targetId);
                personService.updatePerson(targetId, withAliasNote(d, note), changedBy);
            }
            case DELETE -> {
                Long targetId = requireTargetId(target, cr.getId());
                personService.deletePerson(targetId, changedBy);
            }
            case OTHER -> {
                // free-form request — no automatic mutation, admin acts on it outside the system
            }
        }
    }

    private static Long requireTargetId(Person target, Long requestId) {
        if (target == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Change request " + requestId + " has no target person");
        }
        return target.getId();
    }

    private static String requireString(Map<String, Object> data, String key, Long requestId) {
        Object value = data.get(key);
        if (!(value instanceof String s) || s.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Change request " + requestId + " is missing proposedData." + key);
        }
        return s;
    }

    private static PersonUpdateRequest withName(PersonDetailDto d, String name) {
        return new PersonUpdateRequest(
                d.parentId(), name, d.aliasNote(), d.directLine(), d.issueless(), d.unconfirmed(), d.pending(),
                d.birthDate(), d.deathDate(), d.dateIsApproximate(), d.photoUrl());
    }

    private static PersonUpdateRequest withAliasNote(PersonDetailDto d, String aliasNote) {
        return new PersonUpdateRequest(
                d.parentId(), d.name(), aliasNote, d.directLine(), d.issueless(), d.unconfirmed(), d.pending(),
                d.birthDate(), d.deathDate(), d.dateIsApproximate(), d.photoUrl());
    }

    private ChangeRequestDto toDto(ChangeRequest cr) {
        Person target = cr.getTargetPerson();
        return new ChangeRequestDto(
                cr.getId(),
                target != null ? target.getId() : null,
                target != null ? target.getName() : null,
                cr.getRequestType(),
                cr.getProposedData(),
                cr.getRequesterName(),
                cr.getRequesterContact(),
                cr.getModeration().getStatus().name(),
                cr.getModeration().getAdminNotes(),
                cr.getModeration().getReviewedAt(),
                cr.getCreatedAt());
    }
}
