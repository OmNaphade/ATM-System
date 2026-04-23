package com.omnaphade.service;


import com.omnaphade.dtos.CreateUserRequest;
import com.omnaphade.dtos.UserDTO;

import java.util.List;

public interface IUserService {
	List<UserDTO> getAllUsers();

	UserDTO getUserById(Long id);

	UserDTO addUser(CreateUserRequest userReq);

	UserDTO updateUser(Long id, CreateUserRequest userReq);

	String deleteUser(Long id);

	void deleteAllUsers();
}
