package in.ibrahimabad.vanshawali.gallery;

import in.ibrahimabad.vanshawali.common.moderation.ModerationDecisionRequest;
import in.ibrahimabad.vanshawali.gallery.dto.GalleryCommentDto;
import in.ibrahimabad.vanshawali.gallery.dto.GalleryCommentSubmitRequest;
import in.ibrahimabad.vanshawali.gallery.dto.GalleryPostDto;
import in.ibrahimabad.vanshawali.gallery.dto.GalleryPostSubmitRequest;
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
 * Listing approved posts/comments and submitting new ones is public; everything else
 * (pending inbox, approve/reject) requires an admin JWT (enforced in SecurityConfig).
 */
@RestController
@RequestMapping("/api/gallery")
public class GalleryController {

    private final GalleryPostService postService;
    private final GalleryCommentService commentService;

    public GalleryController(GalleryPostService postService, GalleryCommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping
    public List<GalleryPostDto> listApproved() {
        return postService.listApprovedDtos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GalleryPostDto submitPost(@Valid @RequestBody GalleryPostSubmitRequest req) {
        return postService.submit(req);
    }

    @GetMapping("/pending")
    public List<GalleryPostDto> listPending() {
        return postService.listPendingDtos();
    }

    @GetMapping("/all")
    public List<GalleryPostDto> listAll() {
        return postService.listAllDtos();
    }

    @PostMapping("/{id}/approve")
    public GalleryPostDto approvePost(@PathVariable Long id, @RequestBody(required = false) ModerationDecisionRequest body) {
        return postService.approveRequest(id, body != null ? body.notes() : null);
    }

    @PostMapping("/{id}/reject")
    public GalleryPostDto rejectPost(@PathVariable Long id, @RequestBody(required = false) ModerationDecisionRequest body) {
        return postService.rejectRequest(id, body != null ? body.notes() : null);
    }

    @GetMapping("/{id}/comments")
    public List<GalleryCommentDto> listApprovedComments(@PathVariable Long id) {
        return commentService.listApprovedForPost(id);
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public GalleryCommentDto submitComment(@PathVariable Long id, @Valid @RequestBody GalleryCommentSubmitRequest req) {
        return commentService.submit(id, req);
    }

    @GetMapping("/comments/pending")
    public List<GalleryCommentDto> listPendingComments() {
        return commentService.listPendingDtos();
    }

    @PostMapping("/comments/{id}/approve")
    public GalleryCommentDto approveComment(
            @PathVariable Long id, @RequestBody(required = false) ModerationDecisionRequest body) {
        return commentService.approveRequest(id, body != null ? body.notes() : null);
    }

    @PostMapping("/comments/{id}/reject")
    public GalleryCommentDto rejectComment(
            @PathVariable Long id, @RequestBody(required = false) ModerationDecisionRequest body) {
        return commentService.rejectRequest(id, body != null ? body.notes() : null);
    }
}
