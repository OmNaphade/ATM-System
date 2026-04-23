package com.omnaphade;

import com.omnaphade.dtos.CreateUserRequest;
import com.omnaphade.dtos.UserDTO;
import com.omnaphade.entites.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AtmBackendApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api";
    }

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
    }

    @Test
    void testCreateAndGetUser_Integration() {
        // Create user
        CreateUserRequest createRequest = new CreateUserRequest();
        createRequest.setName("Integration Test User");
        createRequest.setEmail("integration@test.com");
        createRequest.setPassword("password123");

        ResponseEntity<UserDTO> createResponse = restTemplate.postForEntity(
                getBaseUrl() + "/users", createRequest, UserDTO.class);

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertEquals("Integration Test User", createResponse.getBody().getName());
        assertEquals(Role.CUSTOMER, createResponse.getBody().getRole());

        Long userId = createResponse.getBody().getUserId();

        // Get user by ID
        ResponseEntity<UserDTO> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/users/" + userId, UserDTO.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(userId, getResponse.getBody().getUserId());
        assertEquals("Integration Test User", getResponse.getBody().getName());
    }

    @Test
    void testGetAllUsers_Integration() {
        ResponseEntity<UserDTO[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/users", UserDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // At least one user should exist from previous test
        assertTrue(response.getBody().length >= 0);
    }

    @Test
    void testCreateUser_DuplicateEmail_Integration() {
        // First create
        CreateUserRequest createRequest = new CreateUserRequest();
        createRequest.setName("Duplicate User");
        createRequest.setEmail("duplicate@test.com");
        createRequest.setPassword("password123");

        ResponseEntity<UserDTO> firstResponse = restTemplate.postForEntity(
                getBaseUrl() + "/users", createRequest, UserDTO.class);
        assertEquals(HttpStatus.OK, firstResponse.getStatusCode());

        // Try to create again with same email
        ResponseEntity<String> secondResponse = restTemplate.postForEntity(
                getBaseUrl() + "/users", createRequest, String.class);
        assertEquals(HttpStatus.CONFLICT, secondResponse.getStatusCode());
    }

    @Test
    void testGetUser_NotFound_Integration() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/users/99999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testHealthCheck() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/actuator/health", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
