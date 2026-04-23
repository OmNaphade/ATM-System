package com.omnaphade.service;

import com.omnaphade.custom_exception.ResourceExistsException;
import com.omnaphade.custom_exception.ResourceNotFoundException;
import com.omnaphade.dtos.CreateUserRequest;
import com.omnaphade.dtos.UserDTO;
import com.omnaphade.entites.Role;
import com.omnaphade.entites.User;
import com.omnaphade.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private DatabaseMetaData metaData;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private CreateUserRequest createRequest;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() throws Exception {
        user = new User();
        user.setUserId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        createRequest = new CreateUserRequest();
        createRequest.setName("John Doe");
        createRequest.setEmail("john@example.com");
        createRequest.setPassword("password");

        userDTO = new UserDTO();
        userDTO.setUserId(1L);
        userDTO.setName("John Doe");
        userDTO.setEmail("john@example.com");
        userDTO.setRole(Role.CUSTOMER);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metaData);
        when(metaData.getURL()).thenReturn("jdbc:postgresql://localhost:5432/atm_db");
    }

    @Test
    void testGetAllUsers_Success() {
        List<User> users = Arrays.asList(user);
        when(userRepo.findAll()).thenReturn(users);

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(userRepo, times(1)).findAll();
    }

    @Test
    void testGetAllUsers_EmptyList() {
        when(userRepo.findAll()).thenReturn(Arrays.asList());

        List<UserDTO> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
        verify(userRepo, times(1)).findAll();
    }

    @Test
    void testGetUserById_Success() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(1L);

        assertEquals("John Doe", result.getName());
        verify(userRepo, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepo, times(1)).findById(1L);
    }

    @Test
    void testAddUser_Success() {
        when(userRepo.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepo.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.addUser(createRequest);

        assertEquals("John Doe", result.getName());
        verify(userRepo, times(1)).existsByEmail("john@example.com");
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void testAddUser_EmailExists() {
        when(userRepo.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(ResourceExistsException.class, () -> userService.addUser(createRequest));
        verify(userRepo, times(1)).existsByEmail("john@example.com");
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void testAddUser_NullName() {
        createRequest.setName(null);

        when(userRepo.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepo.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.addUser(createRequest);

        assertNotNull(result);
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_Success() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        CreateUserRequest updateRequest = new CreateUserRequest();
        updateRequest.setName("Jane Doe");
        updateRequest.setEmail("jane@example.com");
        updateRequest.setPassword("newpassword");

        UserDTO result = userService.updateUser(1L, updateRequest);

        assertEquals("Jane Doe", result.getName());
        assertEquals("jane@example.com", result.getEmail());
        verify(userRepo, times(1)).findById(1L);
    }

    @Test
    void testUpdateUser_NotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, createRequest));
        verify(userRepo, times(1)).findById(1L);
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepo.existsById(1L)).thenReturn(true);

        String result = userService.deleteUser(1L);

        assertEquals("User with ID 1 deleted successfully!", result);
        verify(userRepo, times(1)).existsById(1L);
        verify(userRepo, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepo.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepo, times(1)).existsById(1L);
        verify(userRepo, never()).deleteById(1L);
    }

    @Test
    void testDeleteAllUsers() {
        userService.deleteAllUsers();

        verify(userRepo, times(1)).deleteAll();
    }

    @Test
    void testDataSourceConnection() throws Exception {
        userService.getAllUsers();

        verify(dataSource, times(1)).getConnection();
        verify(connection, times(1)).getMetaData();
    }
}
