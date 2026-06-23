package in.ibrahimabad.vanshawali.gallery;

import in.ibrahimabad.vanshawali.common.moderation.ModerationServiceBase;
import in.ibrahimabad.vanshawali.common.moderation.Status;
import in.ibrahimabad.vanshawali.gallery.dto.GalleryPostDto;
import in.ibrahimabad.vanshawali.gallery.dto.GalleryPostSubmitRequest;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GalleryPostService extends ModerationServiceBase<GalleryPost, Long> {

    private final GalleryPostRepository repository;
    private final GalleryCommentRepository commentRepository;

    public GalleryPostService(GalleryPostRepository repository, GalleryCommentRepository commentRepository) {
        super(repository);
        this.repository = repository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public GalleryPostDto submit(GalleryPostSubmitRequest req) {
        GalleryPost post = new GalleryPost();
        post.setPhotoUrl(req.photoUrl());
        post.setCaption(req.caption());
        post.setUploaderName(req.uploaderName());
        post.setUploaderContact(req.uploaderContact());
        return toDto(repository.save(post));
    }

    public List<GalleryPostDto> listApprovedDtos() {
        return repository.findByModeration_StatusOrderByCreatedAtDesc(Status.APPROVED).stream()
                .map(this::toDto)
                .toList();
    }

    public List<GalleryPostDto> listPendingDtos() {
        return listPending().stream().map(this::toDto).toList();
    }

    public List<GalleryPostDto> listAllDtos() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(GalleryPost::getCreatedAt).reversed())
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public GalleryPostDto approveRequest(Long id, String notes) {
        return toDto(approve(id, notes));
    }

    @Transactional
    public GalleryPostDto rejectRequest(Long id, String notes) {
        return toDto(reject(id, notes));
    }

    private GalleryPostDto toDto(GalleryPost p) {
        long approvedComments = commentRepository.countByPost_IdAndModeration_Status(p.getId(), Status.APPROVED);
        return new GalleryPostDto(
                p.getId(),
                p.getPhotoUrl(),
                p.getCaption(),
                p.getUploaderName(),
                p.getUploaderContact(),
                p.getModeration().getStatus().name(),
                p.getModeration().getAdminNotes(),
                p.getModeration().getReviewedAt(),
                p.getCreatedAt(),
                approvedComments);
    }
}
