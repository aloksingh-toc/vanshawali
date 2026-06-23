package in.ibrahimabad.vanshawali.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Bootstraps the single initial ADMIN account on first run, since only an ADMIN
 * can create other app_users and the table starts empty. Safe to leave in place:
 * it no-ops once any user exists.
 */
@Component
public class AdminUserSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserSeeder.class);

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;
    private final String adminDisplayName;

    public AdminUserSeeder(
            AppUserRepository repository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.username}") String adminUsername,
            @Value("${app.admin.password}") String adminPassword,
            @Value("${app.admin.display-name}") String adminDisplayName) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.adminDisplayName = adminDisplayName;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            log.info("app_users table already has {} rows, skipping admin bootstrap", repository.count());
            return;
        }

        AppUser admin = new AppUser();
        admin.setUsername(adminUsername);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setDisplayName(adminDisplayName);
        admin.setRole(Role.ADMIN);
        repository.save(admin);
        log.info("Bootstrapped initial admin account '{}'", adminUsername);
    }
}
