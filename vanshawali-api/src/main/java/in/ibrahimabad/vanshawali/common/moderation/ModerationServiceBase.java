package in.ibrahimabad.vanshawali.common.moderation;

import java.util.List;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/**
 * Generic submit -> pending -> admin review -> approve/reject service.
 * Extend this once per moderated entity (change requests, gallery posts/comments, announcements)
 * instead of re-implementing the same approve/reject/listPending logic each time.
 */
public abstract class ModerationServiceBase<T extends Moderatable, ID> {

    private final ModerationRepository<T, ID> repository;

    protected ModerationServiceBase(ModerationRepository<T, ID> repository) {
        this.repository = repository;
    }

    public List<T> listPending() {
        return repository.findByModeration_StatusOrderByCreatedAtAsc(Status.PENDING);
    }

    public T approve(ID id, String notes) {
        T entity = findOrThrow(id);
        entity.getModeration().approve(notes);
        return repository.save(entity);
    }

    public T reject(ID id, String notes) {
        T entity = findOrThrow(id);
        entity.getModeration().reject(notes);
        return repository.save(entity);
    }

    private T findOrThrow(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found: " + id));
    }
}
