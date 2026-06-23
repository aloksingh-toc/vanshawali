package in.ibrahimabad.vanshawali.user;

import in.ibrahimabad.vanshawali.common.util.Sorting;
import in.ibrahimabad.vanshawali.user.dto.AppUserCreateRequest;
import in.ibrahimabad.vanshawali.user.dto.AppUserDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class AppUserService {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AppUserDto> listAll() {
        return Sorting.byKeyAsc(repository.findAll(), AppUser::getCreatedAt).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public AppUserDto create(AppUserCreateRequest req) {
        if (repository.findByUsername(req.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken: " + req.username());
        }
        AppUser user = new AppUser();
        user.setUsername(req.username());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setDisplayName(req.displayName());
        user.setRole(req.role());
        return toDto(repository.save(user));
    }

    @Transactional
    public void delete(Long id, Long currentUserId) {
        AppUser user = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
        if (user.getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "अपना ही खाता नहीं मिटा सकते");
        }
        if (user.getRole() == Role.ADMIN && repository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .count() <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "अंतिम Admin खाता नहीं मिटाया जा सकता");
        }
        repository.deleteById(id);
    }

    private AppUserDto toDto(AppUser u) {
        return new AppUserDto(u.getId(), u.getUsername(), u.getDisplayName(), u.getRole().name(), u.getCreatedAt());
    }
}
