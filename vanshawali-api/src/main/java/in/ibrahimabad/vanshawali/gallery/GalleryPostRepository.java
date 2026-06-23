package in.ibrahimabad.vanshawali.gallery;

import in.ibrahimabad.vanshawali.common.moderation.ModerationRepository;
import in.ibrahimabad.vanshawali.common.moderation.Status;
import java.util.List;

public interface GalleryPostRepository extends ModerationRepository<GalleryPost, Long> {

    List<GalleryPost> findByModeration_StatusOrderByCreatedAtDesc(Status status);
}
