package challengeme.backend.controller;

import challengeme.backend.dto.UserDTO;
import challengeme.backend.dto.request.create.UserCreateRequest;
import challengeme.backend.mapper.UserMapper;
import challengeme.backend.model.User;
import challengeme.backend.repository.UserRepository;
import challengeme.backend.security.JwtUtils;
import challengeme.backend.security.UserDetailsServiceImpl;
import challengeme.backend.security.AuthTokenFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Dezactivăm filtrele de securitate pentru testele de controller pure
class AuthControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserMapper userMapper;

    // Aceste bean-uri sunt necesare pentru contextul de securitate, chiar dacă nu le folosim direct în test
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private AuthTokenFilter authTokenFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("emailOrUsername", "testUser");
        loginRequest.put("password", "password");

        // Creăm un obiect UserDetails real pentru a evita ClassCastException în controller
        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(
                "testUser",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_user"))
        );

        // Simulăm autentificarea cu succes
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        // Simulăm generarea token-ului
        when(jwtUtils.generateJwtToken(any())).thenReturn("fake-jwt-token");

        // Simulăm găsirea userului în DB și maparea
        User mockUser = new User();
        mockUser.setUsername("testUser");
        mockUser.setEmail("email@test.com");
        when(userRepository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.of(mockUser));
        when(userMapper.toDTO(any())).thenReturn(new UserDTO(null, "testUser", "email@test.com", 0, "user"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.user.username").value("testUser"));
    }

    @Test
    void testSignup_Success() throws Exception {
        // Arrange
        UserCreateRequest request = new UserCreateRequest("newUser", "new@email.com", "Password_123");

        // Simulăm că username-ul și email-ul NU există
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPass");

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print()) // <--- ADAUGĂ ASTA
                .andExpect(status().isOk());
    }

}