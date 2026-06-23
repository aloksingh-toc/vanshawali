package in.ibrahimabad.vanshawali.fund;

import in.ibrahimabad.vanshawali.fund.dto.FundEntryDto;
import in.ibrahimabad.vanshawali.fund.dto.FundEntryWriteRequest;
import in.ibrahimabad.vanshawali.fund.dto.FundSummaryDto;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
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

/**
 * Summary is public (transparent ledger); entry CRUD requires ADMIN or FUND_MANAGER
 * (enforced in SecurityConfig).
 */
@RestController
@RequestMapping("/api/fund")
public class FundController {

    private final FundEntryService service;

    public FundController(FundEntryService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    public FundSummaryDto summary() {
        return service.summary();
    }

    @GetMapping("/entries")
    public List<FundEntryDto> listEntries(
            @RequestParam(required = false) FundEntryType entryType,
            @RequestParam(required = false) Long relatedEventId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.list(entryType, relatedEventId, from, to);
    }

    @PostMapping("/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public FundEntryDto create(@Valid @RequestBody FundEntryWriteRequest req) {
        return service.create(req);
    }

    @PutMapping("/entries/{id}")
    public FundEntryDto update(@PathVariable Long id, @Valid @RequestBody FundEntryWriteRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/entries/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
