package challengeme.backend.controller.integration;

import challengeme.backend.model.Badge;
import challengeme.backend.model.User;
import challengeme.backend.model.UserBadge;
import challengeme.backend.repository.BadgeRepository;
import challengeme.backend.repository.UserBadgeRepository;
import challengeme.backend.repository.UserRepository;
import challengeme.backend.repository.LeaderboardRepository;
import challengeme.backend.dto.request.create.UserBadgeCreateRequest;
import challengeme.backend.dto.request.update.UserBadgeUpdateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.time.LocalDate;
import java.util.UUID;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Importăm configurația de securitate de test
@Import(UserBadgeControllerIntegrationTests.TestSecurityConfig.class)
class UserBadgeControllerIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    private User userA;
    private Badge badgeA;
    private String baseUrl;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .csrf(csrf -> csrf.disable());
            return http.build();
        }
    }

    @BeforeEach
    void setupDependencies() {
        baseUrl = "http://localhost:" + port + "/api/userbadges";

        cleanup();

        User uA = new User(null, "AnaTest", "ana@test.com", "pass_ana", 10);
        userA = userRepository.save(uA);

        Badge bA = new Badge(null, "GoldTest", "Top Performer", "Complete IT");
        badgeA = badgeRepository.save(bA);
    }

    @AfterEach
    void cleanup() {
        userBadgeRepository.deleteAll();
        leaderboardRepository.deleteAll();
        userRepository.deleteAll();
        badgeRepository.deleteAll();
    }

    // --- TESTE CRUD COMPLETE ---

    @Test
    void testFullCrudLifecycle() {
        // DATE
        UUID userId = userA.getId();
        UUID badgeId = badgeA.getId();

        UserBadgeCreateRequest createRequest = new UserBadgeCreateRequest();
        createRequest.setUserId(userId);
        createRequest.setBadgeId(badgeId);

        // 1. CREATE (POST)
        ResponseEntity<Map> postResp = restTemplate.postForEntity(baseUrl, createRequest, Map.class);

        assertThat(postResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(postResp.getBody()).isNotNull();

        String createdIdStr = postResp.getBody().get("id").toString();
        UUID createdId = UUID.fromString(createdIdStr);

        assertThat(postResp.getBody().get("userId").toString()).isEqualTo(userId.toString());

        // 2. GET BY ID
        ResponseEntity<UserBadge> getResp = restTemplate.getForEntity(baseUrl + "/" + createdId, UserBadge.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResp.getBody().getId()).isEqualTo(createdId);

        // 3. UPDATE (PUT)
        LocalDate newDate = LocalDate.now().plusDays(5);
        UserBadgeUpdateRequest updateRequest = new UserBadgeUpdateRequest();
        updateRequest.setDateAwarded(newDate);

        RequestEntity<UserBadgeUpdateRequest> requestEntity = RequestEntity
                .put(baseUrl + "/" + createdId)
                .body(updateRequest);

        ResponseEntity<UserBadge> updateResp = restTemplate.exchange(requestEntity, UserBadge.class);
        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResp.getBody().getDateAwarded()).isEqualTo(newDate);

        UserBadge updatedEntity = userBadgeRepository.findById(createdId).orElseThrow();
        assertThat(updatedEntity.getDateAwarded()).isEqualTo(newDate);

        // 4. DELETE
        restTemplate.delete(baseUrl + "/" + createdId);

        // 5. GET BY ID AFTER DELETE
        ResponseEntity<String> deletedResp = restTemplate.getForEntity(baseUrl + "/" + createdId, String.class);
        assertThat(deletedResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // --- TESTE DE MARGINALIZARE ---

    @Test
    void testGetUserBadgeNotFound() {
        UUID randomId = UUID.randomUUID();
        ResponseEntity<String> resp = restTemplate.getForEntity(baseUrl + "/" + randomId, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testCreate_MissingDependencies() {
        UUID nonExistentUserId = UUID.randomUUID();
        UUID nonExistentBadgeId = UUID.randomUUID();

        UserBadgeCreateRequest createRequest = new UserBadgeCreateRequest();
        createRequest.setUserId(nonExistentUserId);
        createRequest.setBadgeId(nonExistentBadgeId);

        ResponseEntity<Map> postResp = restTemplate.postForEntity(baseUrl, createRequest, Map.class);
        assertThat(postResp.getStatusCode()).isNotEqualTo(HttpStatus.CREATED);
        assertThat(postResp.getStatusCode()).isIn(HttpStatus.NOT_FOUND, HttpStatus.BAD_REQUEST, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}