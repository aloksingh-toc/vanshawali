package in.ibrahimabad.vanshawali.security;

import jakarta.servlet.DispatcherType;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Public read endpoints (tree/search/persons/requests submission) stay open;
 * everything else requires a valid admin/fund-manager JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tree", "/api/persons/**", "/api/search").permitAll()
                        .requestMatchers(
                                HttpMethod.GET, "/api/relation", "/api/anniversaries/upcoming", "/api/on-this-day")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/requests", "/api/uploads").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gallery", "/api/gallery/*/comments").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/gallery", "/api/gallery/*/comments").permitAll()
                        .requestMatchers(
                                HttpMethod.GET, "/api/gallery/pending", "/api/gallery/all", "/api/gallery/comments/pending")
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/gallery/*/approve",
                                "/api/gallery/*/reject",
                                "/api/gallery/comments/*/approve",
                                "/api/gallery/comments/*/reject")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/persons").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/persons/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/persons/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/persons/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/requests/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/requests/*/approve", "/api/requests/*/reject")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/historical-notes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/historical-notes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/historical-notes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/historical-notes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/events").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/events").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/events/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/events/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/announcements").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/announcements").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/announcements/pending", "/api/announcements/all")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/announcements/*/approve", "/api/announcements/*/reject")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/fund/summary").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/fund/entries").hasAnyRole("ADMIN", "FUND_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/fund/entries").hasAnyRole("ADMIN", "FUND_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/fund/entries/**").hasAnyRole("ADMIN", "FUND_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/fund/entries/**").hasAnyRole("ADMIN", "FUND_MANAGER")
                        .requestMatchers("/api/users", "/api/users/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AppUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return new ProviderManager(provider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
