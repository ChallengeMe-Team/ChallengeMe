package challengeme.backend.security;

import challengeme.backend.model.User;
import challengeme.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom implementation of the Spring Security UserDetailsService.
 * This service is used by the authentication manager to retrieve user credentials
 * from the database during the login process.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Locates a user based on a provided identifier, which can be either a username or an email.
     * This dual-identifier approach enhances user experience by providing flexible login options.
     * * @param identifier The username or email entered by the user.
     * @return A UserDetails object containing the user's credentials and authorities.
     * @throws UsernameNotFoundException if no user is found with the given identifier.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // 1. Database lookup: We query both username and email columns simultaneously
        User user = userRepository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with identifier: " + identifier));

        /**
         * 2. Identity Resolution:
         * We build the Spring Security User object. Even if the 'identifier' was an email,
         * we set the actual 'username' as the principal's name to ensure consistency
         * in our JWT tokens and SecurityContext.
         */
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername()) // Sets the formal identity
                .password(user.getPassword())    // Provides the hashed password for comparison
                .authorities("ROLE_USER")         // Assigns a default role for access control
                .build();
    }
}