package in.ibrahimabad.vanshawali.anniversary;

import in.ibrahimabad.vanshawali.anniversary.dto.AnniversaryEntryDto;
import in.ibrahimabad.vanshawali.anniversary.dto.OnThisDayItemDto;
import in.ibrahimabad.vanshawali.historical.HistoricalNote;
import in.ibrahimabad.vanshawali.historical.HistoricalNoteRepository;
import in.ibrahimabad.vanshawali.person.Person;
import in.ibrahimabad.vanshawali.person.PersonRepository;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Combines birth/death dates on {@code persons} with admin-curated {@code historical_notes}
 * into "upcoming anniversaries" (बरसी/birthday reminders) and "on this day" (आज का इतिहास) feeds.
 */
@Service
@Transactional(readOnly = true)
public class AnniversaryService {

    private final PersonRepository personRepository;
    private final HistoricalNoteRepository noteRepository;

    public AnniversaryService(PersonRepository personRepository, HistoricalNoteRepository noteRepository) {
        this.personRepository = personRepository;
        this.noteRepository = noteRepository;
    }

    public List<AnniversaryEntryDto> upcoming(int days) {
        LocalDate today = LocalDate.now();
        List<AnniversaryEntryDto> result = new ArrayList<>();

        for (Person p : personRepository.findAll()) {
            addIfWithin(result, p, p.getBirthDate(), "BIRTHDAY", today, days);
            addIfWithin(result, p, p.getDeathDate(), "DEATH_ANNIVERSARY", today, days);
        }

        result.sort(Comparator.comparingInt(AnniversaryEntryDto::daysUntil));
        return result;
    }

    public List<OnThisDayItemDto> onThisDay() {
        LocalDate today = LocalDate.now();
        List<OnThisDayItemDto> items = new ArrayList<>();

        for (Person p : personRepository.findAll()) {
            if (matchesMonthDay(p.getBirthDate(), today)) {
                items.add(new OnThisDayItemDto(
                        "BIRTHDAY",
                        p.getName() + " का जन्मदिन",
                        null,
                        p.getId(),
                        p.getName(),
                        p.getPhotoUrl(),
                        p.isDateIsApproximate() ? null : today.getYear() - p.getBirthDate().getYear()));
            }
            if (matchesMonthDay(p.getDeathDate(), today)) {
                items.add(new OnThisDayItemDto(
                        "DEATH_ANNIVERSARY",
                        p.getName() + " की पुण्यतिथि (बरसी)",
                        null,
                        p.getId(),
                        p.getName(),
                        p.getPhotoUrl(),
                        p.isDateIsApproximate() ? null : today.getYear() - p.getDeathDate().getYear()));
            }
        }

        for (HistoricalNote note : noteRepository.findByNoteMonthAndNoteDay(today.getMonthValue(), today.getDayOfMonth())) {
            Person related = note.getRelatedPerson();
            items.add(new OnThisDayItemDto(
                    "HISTORICAL_NOTE",
                    note.getTitle(),
                    note.getDescription(),
                    related != null ? related.getId() : null,
                    related != null ? related.getName() : null,
                    note.getPhotoUrl(),
                    null));
        }

        return items;
    }

    private void addIfWithin(
            List<AnniversaryEntryDto> result, Person p, LocalDate date, String type, LocalDate today, int days) {
        if (date == null) {
            return;
        }
        int daysUntil = daysUntilNextOccurrence(date, today);
        if (daysUntil > days) {
            return;
        }
        int yearAtOccurrence = today.plusDays(daysUntil).getYear();
        Integer years = p.isDateIsApproximate() ? null : yearAtOccurrence - date.getYear();
        result.add(new AnniversaryEntryDto(p.getId(), p.getName(), type, date, p.isDateIsApproximate(), daysUntil, years));
    }

    /** Days from today until the next month/day occurrence of {@code originalDate}, handling Feb 29 gracefully. */
    private int daysUntilNextOccurrence(LocalDate originalDate, LocalDate today) {
        MonthDay md = MonthDay.from(originalDate);
        LocalDate thisYear = atYearSafe(md, today.getYear());
        if (!thisYear.isBefore(today)) {
            return (int) ChronoUnit.DAYS.between(today, thisYear);
        }
        LocalDate nextYear = atYearSafe(md, today.getYear() + 1);
        return (int) ChronoUnit.DAYS.between(today, nextYear);
    }

    private LocalDate atYearSafe(MonthDay md, int year) {
        if (md.getMonthValue() == 2 && md.getDayOfMonth() == 29 && !java.time.Year.isLeap(year)) {
            return LocalDate.of(year, 2, 28);
        }
        return md.atYear(year);
    }

    private boolean matchesMonthDay(LocalDate date, LocalDate today) {
        return date != null && date.getMonthValue() == today.getMonthValue() && date.getDayOfMonth() == today.getDayOfMonth();
    }
}
