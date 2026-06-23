package in.ibrahimabad.vanshawali.gallery;

import in.ibrahimabad.vanshawali.common.moderation.ModerationServiceBase;
import in.ibrahimabad.vanshawali.common.moderation.Status;
import in.ibrahimabad.vanshawali.gallery.dto.GalleryCommentDto;
import in.ibrahimabad.vanshawali.gallery.dto.GalleryCommentSubmitRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class GalleryCommentService extends ModerationServiceBase<GalleryComment, Long> {

    private final GalleryCommentRepository repository;
    private final GalleryPostRepository postRepository;

    public GalleryCommentService(GalleryCommentRepository repository, GalleryPostRepository postRepository) {
        super(repository);
        this.repository = repository;
        this.postRepository = postRepository;
    }

    @Transactional
    public GalleryCommentDto submit(Long postId, GalleryCommentSubmitRequest req) {
        GalleryPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found: " + postId));

        GalleryComment comment = new GalleryComment();
        comment.setPost(post);
        comment.setCommenterName(req.commenterName());
        comment.setBody(req.body());
        return toDto(repository.save(comment));
    }

    public List<GalleryCommentDto> listApprovedForPost(Long postId) {
        return repository.findByPost_IdAndModeration_StatusOrderByCreatedAtAsc(postId, Status.APPROVED).stream()
                .map(this::toDto)
                .toList();
    }

    public List<GalleryCommentDto> listPendingDtos() {
        return listPending().stream().map(this::toDto).toList();
    }

    @Transactional
    public GalleryCommentDto approveRequest(Long id, String notes) {
        return toDto(approve(id, notes));
    }

    @Transactional
    public GalleryCommentDto rejectRequest(Long id, String notes) {
        return toDto(reject(id, notes));
    }

    private GalleryCommentDto toDto(GalleryComment c) {
        return new GalleryCommentDto(
                c.getId(),
                c.getPost().getId(),
                c.getCommenterName(),
                c.getBody(),
                c.getModeration().getStatus().name(),
                c.getModeration().getAdminNotes(),
                c.getCreatedAt());
    }
}
