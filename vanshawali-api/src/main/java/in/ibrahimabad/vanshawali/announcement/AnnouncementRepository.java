package in.ibrahimabad.vanshawali.announcement;

import in.ibrahimabad.vanshawali.common.moderation.ModerationRepository;
import in.ibrahimabad.vanshawali.common.moderation.Status;
import java.util.List;

public interface AnnouncementRepository extends ModerationRepository<Announcement, Long> {

    List<Announcement> findByModeration_StatusOrderByCreatedAtDesc(Status status);
}
