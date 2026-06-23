package in.ibrahimabad.vanshawali.announcement;

import in.ibrahimabad.vanshawali.announcement.dto.AnnouncementDto;
import in.ibrahimabad.vanshawali.announcement.dto.AnnouncementSubmitRequest;
import in.ibrahimabad.vanshawali.common.moderation.ModerationDecisionRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Listing approved announcements and submitting new ones is public; everything else
 * (pending inbox, approve/reject) requires an admin JWT (enforced in SecurityConfig).
 */
@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService service;

    public AnnouncementController(AnnouncementService service) {
        this.service = service;
    }

    @GetMapping
    public List<AnnouncementDto> listApproved() {
        return service.listApprovedDtos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnnouncementDto submit(@Valid @RequestBody AnnouncementSubmitRequest req) {
        return service.submit(req);
    }

    @GetMapping("/pending")
    public List<AnnouncementDto> listPending() {
        return service.listPendingDtos();
    }

    @GetMapping("/all")
    public List<AnnouncementDto> listAll() {
        return service.listAllDtos();
    }

    @PostMapping("/{id}/approve")
    public AnnouncementDto approve(@PathVariable Long id, @RequestBody(required = false) ModerationDecisionRequest body) {
        return service.approveRequest(id, body != null ? body.notes() : null);
    }

    @PostMapping("/{id}/reject")
    public AnnouncementDto reject(@PathVariable Long id, @RequestBody(required = false) ModerationDecisionRequest body) {
        return service.rejectRequest(id, body != null ? body.notes() : null);
    }
}
