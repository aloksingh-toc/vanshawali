package in.ibrahimabad.vanshawali.common.crud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Generic find-or-404 / delete-or-404 support for simple admin-managed entities
 * (historical notes, community events, fund entries) that don't go through moderation.
 */
public abstract class CrudServiceBase<T, ID> {

    private final JpaRepository<T, ID> repository;
    private final String entityLabel;

    protected CrudServiceBase(JpaRepository<T, ID> repository, String entityLabel) {
        this.repository = repository;
        this.entityLabel = entityLabel;
    }

    protected T getOrThrow(ID id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, entityLabel + " not found: " + id));
    }

    protected void deleteOrThrow(ID id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, entityLabel + " not found: " + id);
        }
        repository.deleteById(id);
    }
}
