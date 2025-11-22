package challengeme.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("test")
public class TestSecurityConfig {

    @Bean(name = "testFilterChain") // Nume explicit pentru a evita conflictul direct de nume
    @Order(Ordered.HIGHEST_PRECEDENCE) // Prioritate maximă în lanțul de filtre
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .securityMatcher("/api/**") // Aplicăm doar pe API-uri
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}