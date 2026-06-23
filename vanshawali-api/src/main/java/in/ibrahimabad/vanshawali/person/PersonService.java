package in.ibrahimabad.vanshawali.person;

import in.ibrahimabad.vanshawali.audit.AuditLog;
import in.ibrahimabad.vanshawali.audit.AuditLogRepository;
import in.ibrahimabad.vanshawali.person.dto.PersonCreateRequest;
import in.ibrahimabad.vanshawali.person.dto.PersonDetailDto;
import in.ibrahimabad.vanshawali.person.dto.PersonFlagsRequest;
import in.ibrahimabad.vanshawali.person.dto.PersonNodeDto;
import in.ibrahimabad.vanshawali.person.dto.PersonSummaryDto;
import in.ibrahimabad.vanshawali.person.dto.PersonUpdateRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class PersonService {

    private final PersonRepository repository;
    private final AuditLogRepository auditLogRepository;

    public PersonService(PersonRepository repository, AuditLogRepository auditLogRepository) {
        this.repository = repository;
        this.auditLogRepository = auditLogRepository;
    }

    public PersonNodeDto getTree() {
        List<Person> all = repository.findAllByOrderBySiblingOrderAsc();

        Map<Long, List<Person>> childrenByParentId = all.stream()
                .filter(p -> p.getParent() != null)
                .collect(Collectors.groupingBy(p -> p.getParent().getId()));

        Person root = all.stream()
                .filter(p -> p.getParent() == null)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No root person found"));

        return toNode(root, childrenByParentId);
    }

    private PersonNodeDto toNode(Person person, Map<Long, List<Person>> childrenByParentId) {
        List<PersonNodeDto> children = childrenByParentId
                .getOrDefault(person.getId(), List.of())
                .stream()
                .map(child -> toNode(child, childrenByParentId))
                .toList();

        return new PersonNodeDto(
                person.getId(),
                person.getName(),
                person.getAliasNote(),
                person.isDirectLine(),
                person.isIssueless(),
                person.isUnconfirmed(),
                person.isPending(),
                person.getPhotoUrl(),
                children);
    }

    public PersonDetailDto getById(Long id) {
        Person person = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found: " + id));
        return toDetail(person);
    }

    public List<PersonSummaryDto> search(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return repository.findByNameContainingIgnoreCaseOrderByNameAsc(query.trim())
                .stream()
                .map(this::toSummary)
                .toList();
    }

    private PersonDetailDto toDetail(Person person) {
        Person parent = person.getParent();
        return new PersonDetailDto(
                person.getId(),
                person.getName(),
                person.getAliasNote(),
                person.isDirectLine(),
                person.isIssueless(),
                person.isUnconfirmed(),
                person.isPending(),
                person.getBirthDate(),
                person.getDeathDate(),
                person.isDateIsApproximate(),
                person.getPhotoUrl(),
                person.getGeneration(),
                parent != null ? parent.getId() : null,
                parent != null ? parent.getName() : null);
    }

    private PersonSummaryDto toSummary(Person person) {
        Person parent = person.getParent();
        return new PersonSummaryDto(
                person.getId(),
                person.getName(),
                person.getAliasNote(),
                person.isDirectLine(),
                person.getGeneration(),
                parent != null ? parent.getId() : null,
                parent != null ? parent.getName() : null);
    }

    @Transactional
    public PersonDetailDto createPerson(PersonCreateRequest req, String changedBy) {
        Person parent = repository.findById(req.parentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent not found: " + req.parentId()));

        Person person = new Person();
        person.setParent(parent);
        person.setName(req.name());
        person.setAliasNote(req.aliasNote());
        person.setDirectLine(req.directLine());
        person.setIssueless(req.issueless());
        person.setUnconfirmed(req.unconfirmed());
        person.setPending(req.pending());
        person.setBirthDate(req.birthDate());
        person.setDeathDate(req.deathDate());
        person.setDateIsApproximate(req.dateIsApproximate());
        person.setPhotoUrl(req.photoUrl());
        person.setGeneration(parent.getGeneration() + 1);
        person.setSiblingOrder(nextSiblingOrder(parent.getId()));

        Person saved = repository.save(person);
        audit(saved.getId(), "CREATE_PERSON", changedBy, Map.of("name", saved.getName(), "parentId", parent.getId()));
        return toDetail(saved);
    }

    @Transactional
    public PersonDetailDto updatePerson(Long id, PersonUpdateRequest req, String changedBy) {
        Person person = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found: " + id));

        Map<String, Object> diff = new HashMap<>();

        if (req.parentId() != null && person.getParent() != null && !req.parentId().equals(person.getParent().getId())) {
            if (req.parentId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A person cannot be their own parent");
            }
            Person newParent = repository.findById(req.parentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent not found: " + req.parentId()));
            for (Person ancestor = newParent; ancestor != null; ancestor = ancestor.getParent()) {
                if (ancestor.getId().equals(id)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Cannot move a person under one of their own descendants");
                }
            }
            diff.put("parentId", Map.of("from", person.getParent().getId(), "to", newParent.getId()));
            person.setParent(newParent);
            person.setGeneration(newParent.getGeneration() + 1);
            person.setSiblingOrder(nextSiblingOrder(newParent.getId()));
        }

        if (!req.name().equals(person.getName())) {
            diff.put("name", Map.of("from", person.getName(), "to", req.name()));
        }
        person.setName(req.name());
        person.setAliasNote(req.aliasNote());
        person.setDirectLine(req.directLine());
        person.setIssueless(req.issueless());
        person.setUnconfirmed(req.unconfirmed());
        person.setPending(req.pending());
        person.setBirthDate(req.birthDate());
        person.setDeathDate(req.deathDate());
        person.setDateIsApproximate(req.dateIsApproximate());
        person.setPhotoUrl(req.photoUrl());

        Person saved = repository.save(person);
        audit(saved.getId(), "UPDATE_PERSON", changedBy, diff);
        return toDetail(saved);
    }

    @Transactional
    public PersonDetailDto updateFlags(Long id, PersonFlagsRequest req, String changedBy) {
        Person person = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found: " + id));

        Map<String, Object> diff = new HashMap<>();
        if (req.directLine() != null && req.directLine() != person.isDirectLine()) {
            diff.put("directLine", req.directLine());
            person.setDirectLine(req.directLine());
        }
        if (req.issueless() != null && req.issueless() != person.isIssueless()) {
            diff.put("issueless", req.issueless());
            person.setIssueless(req.issueless());
        }
        if (req.unconfirmed() != null && req.unconfirmed() != person.isUnconfirmed()) {
            diff.put("unconfirmed", req.unconfirmed());
            person.setUnconfirmed(req.unconfirmed());
        }
        if (req.pending() != null && req.pending() != person.isPending()) {
            diff.put("pending", req.pending());
            person.setPending(req.pending());
        }

        Person saved = repository.save(person);
        audit(saved.getId(), "UPDATE_FLAGS", changedBy, diff);
        return toDetail(saved);
    }

    @Transactional
    public void deletePerson(Long id, String changedBy) {
        Person person = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found: " + id));

        if (person.getParent() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete the root of the tree");
        }
        if (repository.existsByParent_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete a person with children — reassign or remove them first");
        }

        repository.delete(person);
        audit(id, "DELETE_PERSON", changedBy, Map.of("name", person.getName()));
    }

    private int nextSiblingOrder(Long parentId) {
        List<Person> siblings = repository.findByParent_IdOrderBySiblingOrderAsc(parentId);
        return siblings.stream().mapToInt(Person::getSiblingOrder).max().orElse(-1) + 1;
    }

    private void audit(Long personId, String action, String changedBy, Map<String, Object> diff) {
        AuditLog log = new AuditLog();
        log.setPersonId(personId);
        log.setAction(action);
        log.setChangedBy(changedBy);
        log.setDiff(diff);
        auditLogRepository.save(log);
    }
}
