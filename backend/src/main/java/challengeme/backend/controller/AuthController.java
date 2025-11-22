package challengeme.backend.controller;

import challengeme.backend.dto.request.create.UserCreateRequest;
import challengeme.backend.mapper.UserMapper;
import challengeme.backend.model.User;
import challengeme.backend.repository.UserRepository;
import challengeme.backend.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        String emailOrUsername = loginRequest.get("emailOrUsername");
        String password = loginRequest.get("password");

        // Autentificam userul folosind Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(emailOrUsername, password));

        // Setam contextul de securitate
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generam Token-ul JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Luam detaliile userului autentificat
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Cautam userul complet in baza de date pentru a-l returna frontend-ului (cu puncte, rol, ID)
        User user = userRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: User found in context but not in DB."));

        // Construim raspunsul: Token + UserDTO
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("user", userMapper.toDTO(user));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreateRequest signUpRequest) {
        // Verificari existente
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        // Creare user nou
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword())); // Hash parola
        user.setPoints(0);
        user.setRole("user"); // Setare rol default

        userRepository.save(user);

        // Returnăm un Map (JSON) în loc de String simplu
        return ResponseEntity.ok(Collections.singletonMap("message", "User registered successfully!"));

    }

    // --- Endpoint NOU pentru persistenta login-ului la refresh (F5) ---
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        return ResponseEntity.ok(userMapper.toDTO(user));
    }
}