package challengeme.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for managing JSON Web Tokens (JWT).
 * It handles the generation, parsing, and validation of tokens using HS256 encryption.
 * This class ensures that the identity of the user is securely transmitted between
 * the frontend and backend.
 */
@Component
public class JwtUtils {

    /** Secret key used for signing the tokens, injected from application properties. */
    @Value("${challengeMe.app.jwtSecret}")
    private String jwtSecret;

    /** Token validity duration in milliseconds, injected from application properties. */
    @Value("${challengeMe.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Generates a new JWT token based on a successful authentication.
     * Extracts the username from the Principal and sets the issuance and expiration dates.
     * @param authentication The Spring Security authentication object.
     * @return A signed JWT string.
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Reconstructs the cryptographic key from the Base64 encoded secret.
     * @return A SecretKey object compatible with HMAC-SHA algorithms.
     */
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Extracts the username (subject) from a provided JWT string.
     * @param token The raw JWT string.
     * @return The username contained within the token claims.
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Validates the integrity and expiration of a JWT token.
     * Verifies the signature against the local secret key.
     * @param authToken The token to be validated.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            // Logs cryptographic or expiration errors for security auditing
            System.err.println("Invalid JWT signature: " + e.getMessage());
        }
        return false;
    }

    /**
     * Generates a JWT token directly from a username.
     * Critical for profile updates: if a user changes their username,
     * the system must issue a new token to keep the session valid.
     * @param username The username to be encoded in the token.
     * @return A new signed JWT string.
     */
    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }
}
