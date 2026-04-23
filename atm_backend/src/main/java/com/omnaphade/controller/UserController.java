package com.omnaphade.controller;

import com.omnaphade.dtos.CreateUserRequest;
import com.omnaphade.dtos.UserDTO;
import com.omnaphade.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/users")
public class UserController {


    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all Users")
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Get User by ID")
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Create User")
    @PostMapping
    public UserDTO createUser(@RequestBody CreateUserRequest userReq) {
        return userService.addUser(userReq);
    }

    @Operation(summary = "Update User")
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody CreateUserRequest userReq) {
        return userService.updateUser(id, userReq);
    }

    @Operation(summary = "Delete User")
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    @Operation(summary = "Delete All Users")
    @DeleteMapping("/reset")
    public String deleteAllUser() { 
        userService.deleteAllUsers();
        return "Delete all users initiated asynchronously.";
    }
}

