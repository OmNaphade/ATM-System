package com.omnaphade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnaphade.dtos.CreateUserRequest;
import com.omnaphade.dtos.UserDTO;
import com.omnaphade.entites.Role;
import com.omnaphade.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;
    private CreateUserRequest createRequest;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setUserId(1L);
        userDTO.setName("John Doe");
        userDTO.setEmail("john@example.com");
        userDTO.setRole(Role.CUSTOMER);

        createRequest = new CreateUserRequest();
        createRequest.setName("John Doe");
        createRequest.setEmail("john@example.com");
        createRequest.setPassword("password");
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        List<UserDTO> users = Arrays.asList(userDTO);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("John Doe"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(1L)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testCreateUser_Success() throws Exception {
        when(userService.addUser(any(CreateUserRequest.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(userService, times(1)).addUser(any(CreateUserRequest.class));
    }

    @Test
    void testCreateUser_InvalidRequest() throws Exception {
        createRequest.setEmail(""); // Invalid email

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).addUser(any(CreateUserRequest.class));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        when(userService.updateUser(eq(1L), any(CreateUserRequest.class))).thenReturn(userDTO);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(userService, times(1)).updateUser(eq(1L), any(CreateUserRequest.class));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        when(userService.deleteUser(1L)).thenReturn("User deleted");

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted"));

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void testDeleteAllUsers() throws Exception {
        doNothing().when(userService).deleteAllUsers();

        mockMvc.perform(delete("/api/users/reset"))
                .andExpect(status().isOk())
                .andExpect(content().string("Delete all users initiated asynchronously."));

        verify(userService, times(1)).deleteAllUsers();
    }

    @Test
    void testCreateUser_EmailExists() throws Exception {
        when(userService.addUser(any(CreateUserRequest.class))).thenThrow(new RuntimeException("Email already exists"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).addUser(any(CreateUserRequest.class));
    }
}
