package in.ibrahimabad.vanshawali.changerequest;

import in.ibrahimabad.vanshawali.changerequest.dto.ChangeRequestDto;
import in.ibrahimabad.vanshawali.changerequest.dto.ChangeRequestSubmitRequest;
import in.ibrahimabad.vanshawali.common.moderation.ModerationDecisionRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Submitting a request is public; listing/approving/rejecting requires an admin JWT (enforced in SecurityConfig). */
@RestController
@RequestMapping("/api/requests")
public class ChangeRequestController {

    private final ChangeRequestService service;

    public ChangeRequestController(ChangeRequestService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChangeRequestDto submit(@Valid @RequestBody ChangeRequestSubmitRequest req) {
        return service.submit(req);
    }

    @GetMapping("/pending")
    public List<ChangeRequestDto> listPending() {
        return service.listPendingDtos();
    }

    @GetMapping
    public List<ChangeRequestDto> listAll() {
        return service.listAllDtos();
    }

    @PostMapping("/{id}/approve")
    public ChangeRequestDto approve(
            @PathVariable Long id,
            @RequestBody(required = false) ModerationDecisionRequest body,
            Authentication auth) {
        String notes = body != null ? body.notes() : null;
        return service.approveAndApply(id, notes, auth.getName());
    }

    @PostMapping("/{id}/reject")
    public ChangeRequestDto reject(
            @PathVariable Long id, @RequestBody(required = false) ModerationDecisionRequest body) {
        String notes = body != null ? body.notes() : null;
        return service.rejectRequest(id, notes);
    }
}
