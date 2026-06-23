package in.ibrahimabad.vanshawali.common.moderation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/** Repository contract shared by every moderated entity (change requests, gallery posts/comments, announcements). */
@NoRepositoryBean
public interface ModerationRepository<T extends Moderatable, ID> extends JpaRepository<T, ID> {

    List<T> findByModeration_StatusOrderByCreatedAtAsc(Status status);
}
