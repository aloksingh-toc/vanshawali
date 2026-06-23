package in.ibrahimabad.vanshawali.gallery;

import in.ibrahimabad.vanshawali.common.moderation.ModerationRepository;
import in.ibrahimabad.vanshawali.common.moderation.Status;
import java.util.List;

public interface GalleryCommentRepository extends ModerationRepository<GalleryComment, Long> {

    List<GalleryComment> findByPost_IdAndModeration_StatusOrderByCreatedAtAsc(Long postId, Status status);

    long countByPost_IdAndModeration_Status(Long postId, Status status);
}
