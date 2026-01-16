package challengeme.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Security filter that intercepts every incoming HTTP request to validate JWT tokens.
 * It extends OncePerRequestFilter to guarantee a single execution per request dispatch.
 * This class is responsible for "authenticating" the user based on the Bearer token provided in the headers.
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Core filtering logic. It extracts the JWT, validates it, and sets the
     * authentication in the SecurityContextHolder if the token is valid.
     * * @param request The incoming HTTP request.
     * @param response The HTTP response.
     * @param filterChain The chain of subsequent filters to be executed.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. Extract the token from the Authorization header
            String jwt = parseJwt(request);

            // 2. Validate token integrity and expiration
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // 3. Load user details from the database using the username from the token
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 4. Create an authentication object and populate it with user authorities (roles)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                // 5. Build security details based on the current web request
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Set the authentication in the global Security Context for the current thread
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Log security failures without interrupting the filter chain
            logger.error("Cannot set user authentication: {}", e);
        }

        // Continue with the next filter in the Spring Security chain
        filterChain.doFilter(request, response);
    }

    /**
     * Helper method to parse the 'Authorization' header and remove the 'Bearer ' prefix.
     * * @param request The HTTP request containing the headers.
     * @return The raw JWT string, or null if the header is missing or malformed.
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Skip "Bearer " prefix
        }

        return null;
    }
}