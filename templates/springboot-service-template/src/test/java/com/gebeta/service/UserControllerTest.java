package com.gebeta.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gebeta.service.dto.UserUpdateRequest;
import com.gebeta.service.model.User;
import com.gebeta.service.repository.UserRepository;
import com.gebeta.service.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for User Controller.
 * Follows Gebeta Sovereign Coding Rules for security and quality.
 * 
 * Test coverage:
 * - GET /users/me (success, unauthorized, invalid token, expired token)
 * - PUT /users/me (full update, partial update, weak password, duplicate email)
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private String authToken;
    private Long userId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        // Create test user
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder.encode("StrongPass123!"));
        user.setFullName("Test User");
        user.setCreatedAt(LocalDateTime.now());
        User saved = userRepository.save(user);
        userId = saved.getId();
        
        authToken = jwtUtil.generateToken(user.getEmail());
    }

    // ========== GET /users/me Tests ==========

    @Test
    void getCurrentUser_Success() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void getCurrentUser_Unauthorized_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_InvalidToken_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_ExpiredToken_Returns401() throws Exception {
        // Create an expired token (using a past date)
        String expiredToken = jwtUtil.generateTokenWithExpiration("user@example.com", -1000L);
        
        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    // ========== PUT /users/me Tests ==========

    @Test
    void updateCurrentUser_FullUpdate_Success() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest(
            "updated@example.com",
            "Updated Name",
            "NewStrongPass123!"
        );

        mockMvc.perform(put("/api/v1/users/me")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.fullName").value("Updated Name"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void updateCurrentUser_PartialUpdate_OnlyEmail() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("partial@example.com");

        mockMvc.perform(put("/api/v1/users/me")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("partial@example.com"))
                .andExpect(jsonPath("$.fullName").value("Test User")); // Unchanged
    }

    @Test
    void updateCurrentUser_PartialUpdate_OnlyFullName() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFullName("Only Name Changed");

        mockMvc.perform(put("/api/v1/users/me")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Only Name Changed"))
                .andExpect(jsonPath("$.email").value("user@example.com")); // Unchanged
    }

    @Test
    void updateCurrentUser_PartialUpdate_OnlyPassword() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setPassword("PartialUpdatePass123!");

        mockMvc.perform(put("/api/v1/users/me")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
        
        // Verify old login fails, new password works
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"user@example.com\",\"password\":\"StrongPass123!\"}"))
                .andExpect(status().isUnauthorized());
        
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"user@example.com\",\"password\":\"PartialUpdatePass123!\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateCurrentUser_WeakPassword_Returns400() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setPassword("123");

        mockMvc.perform(put("/api/v1/users/me")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCurrentUser_DuplicateEmail_Returns400() throws Exception {
        // Create another user
        User other = new User();
        other.setEmail("existing@example.com");
        other.setPassword(passwordEncoder.encode("StrongPass123!"));
        other.setCreatedAt(LocalDateTime.now());
        userRepository.save(other);

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("existing@example.com");

        mockMvc.perform(put("/api/v1/users/me")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCurrentUser_InvalidEmail_Returns400() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("not-an-email");

        mockMvc.perform(put("/api/v1/users/me")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCurrentUser_Unauthorized_Returns401() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("hacker@example.com");

        mockMvc.perform(put("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized());
    }
}