package in.ibrahimabad.vanshawali.fund;

import in.ibrahimabad.vanshawali.common.crud.CrudServiceBase;
import in.ibrahimabad.vanshawali.event.CommunityEvent;
import in.ibrahimabad.vanshawali.event.CommunityEventRepository;
import in.ibrahimabad.vanshawali.fund.dto.FundEntryDto;
import in.ibrahimabad.vanshawali.fund.dto.FundEntryWriteRequest;
import in.ibrahimabad.vanshawali.fund.dto.FundEventBreakdownDto;
import in.ibrahimabad.vanshawali.fund.dto.FundSummaryDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class FundEntryService extends CrudServiceBase<FundEntry, Long> {

    private final FundEntryRepository repository;
    private final CommunityEventRepository eventRepository;

    public FundEntryService(FundEntryRepository repository, CommunityEventRepository eventRepository) {
        super(repository, "Fund entry");
        this.repository = repository;
        this.eventRepository = eventRepository;
    }

    public List<FundEntryDto> list(FundEntryType entryType, Long relatedEventId, LocalDate from, LocalDate to) {
        List<FundEntry> entries;
        if (entryType != null && relatedEventId != null) {
            entries = repository.findByEntryTypeAndRelatedEvent_IdOrderByEntryDateDescCreatedAtDesc(
                    entryType, relatedEventId);
        } else if (entryType != null) {
            entries = repository.findByEntryTypeOrderByEntryDateDescCreatedAtDesc(entryType);
        } else if (relatedEventId != null) {
            entries = repository.findByRelatedEvent_IdOrderByEntryDateDescCreatedAtDesc(relatedEventId);
        } else {
            entries = repository.findAllByOrderByEntryDateDescCreatedAtDesc();
        }
        return entries.stream()
                .filter(e -> from == null || !e.getEntryDate().isBefore(from))
                .filter(e -> to == null || !e.getEntryDate().isAfter(to))
                .map(this::toDto)
                .toList();
    }

    public FundSummaryDto summary() {
        List<FundEntry> all = repository.findAll();

        BigDecimal totalContributions = sumOf(all, FundEntryType.CONTRIBUTION, null);
        BigDecimal totalExpenses = sumOf(all, FundEntryType.EXPENSE, null);

        Map<Long, CommunityEvent> eventsById = new LinkedHashMap<>();
        for (FundEntry e : all) {
            if (e.getRelatedEvent() != null) {
                eventsById.putIfAbsent(e.getRelatedEvent().getId(), e.getRelatedEvent());
            }
        }

        List<FundEventBreakdownDto> byEvent = eventsById.values().stream()
                .map(ev -> {
                    BigDecimal contributions = sumOf(all, FundEntryType.CONTRIBUTION, ev.getId());
                    BigDecimal expenses = sumOf(all, FundEntryType.EXPENSE, ev.getId());
                    return new FundEventBreakdownDto(
                            ev.getId(), ev.getTitle(), contributions, expenses, contributions.subtract(expenses));
                })
                .sorted(Comparator.comparing(FundEventBreakdownDto::eventId))
                .toList();

        return new FundSummaryDto(
                totalContributions, totalExpenses, totalContributions.subtract(totalExpenses), byEvent);
    }

    @Transactional
    public FundEntryDto create(FundEntryWriteRequest req) {
        FundEntry entry = new FundEntry();
        apply(entry, req);
        return toDto(repository.save(entry));
    }

    @Transactional
    public FundEntryDto update(Long id, FundEntryWriteRequest req) {
        FundEntry entry = getOrThrow(id);
        apply(entry, req);
        return toDto(repository.save(entry));
    }

    @Transactional
    public void delete(Long id) {
        deleteOrThrow(id);
    }

    private BigDecimal sumOf(List<FundEntry> entries, FundEntryType type, Long relatedEventId) {
        return entries.stream()
                .filter(e -> e.getEntryType() == type)
                .filter(e -> relatedEventId == null || (e.getRelatedEvent() != null && e.getRelatedEvent().getId().equals(relatedEventId)))
                .map(FundEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void apply(FundEntry entry, FundEntryWriteRequest req) {
        entry.setName(req.name());
        entry.setAmount(req.amount());
        entry.setEntryDate(req.entryDate());
        entry.setMode(req.mode());
        entry.setNote(req.note());
        entry.setEntryType(req.entryType());
        entry.setReceiptUrl(req.receiptUrl());
        if (req.relatedEventId() != null) {
            CommunityEvent event = eventRepository.findById(req.relatedEventId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Event not found: " + req.relatedEventId()));
            entry.setRelatedEvent(event);
        } else {
            entry.setRelatedEvent(null);
        }
    }

    private FundEntryDto toDto(FundEntry e) {
        CommunityEvent ev = e.getRelatedEvent();
        return new FundEntryDto(
                e.getId(),
                e.getName(),
                e.getAmount(),
                e.getEntryDate(),
                e.getMode().name(),
                e.getNote(),
                e.getEntryType().name(),
                ev != null ? ev.getId() : null,
                ev != null ? ev.getTitle() : null,
                e.getReceiptUrl(),
                e.getCreatedAt());
    }
}
