//package challengeme.backend.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//@EnableWebSecurity
//public class TestSecurityConfig {
//
//    /**
//     * Creează un utilizator in-memory PENTRU TESTE.
//     */
//    @Bean
//    public InMemoryUserDetailsManager userDetailsManager() {
//
//        // --- AICI A FOST CORECTURA ---
//        // Am înlocuit .withDefaultPasswordEncoder()
//        // cu .withUsername() și am adăugat prefixul {noop} la parolă.
//        // Asta îi spune lui Spring Security să trateze "testpass" ca plain text.
//
//        UserDetails user = User.withUsername("testuser")
//                .password("{noop}testpass")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }
//
//    /**
//     * Configurează lanțul de securitate.
//     */
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authz -> authz
//                        .anyRequest().authenticated() // Cere autentificare pentru TOATE endpoint-urile
//                )
//                .httpBasic(withDefaults()) // Activează autentificarea Basic Auth (pentru testuser/testpass)
//                .csrf(csrf -> csrf.disable()); // Dezactivează CSRF (crucial pentru TestRestTemplate la POST/PATCH)
//        return http.build();
//    }
//}
package challengeme.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("test") // se activează doar pentru profilul 'test'
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // dezactivează CSRF
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // permite toate request-urile

        return http.build();
    }
}