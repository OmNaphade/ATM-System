package com.omnaphade.service;

import com.omnaphade.custom_exception.ResourceExistsException;
import com.omnaphade.custom_exception.ResourceNotFoundException;
import com.omnaphade.dtos.CreateUserRequest;
import com.omnaphade.dtos.UserDTO;
import com.omnaphade.entites.Role;
import com.omnaphade.entites.User;
import com.omnaphade.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements IUserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	private final UserRepository userRepo;
	private final DataSource dataSource;
	private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepo, DataSource dataSource, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.dataSource = dataSource;
        this.passwordEncoder = passwordEncoder;
    }

    private UserDTO mapToDTO(User user) {
		UserDTO dto = new UserDTO();
		dto.setUserId(user.getUserId());
		dto.setName(user.getName());
		dto.setEmail(user.getEmail());
		dto.setRole(user.getRole());
		return dto;
	}

	@Override
	public List<UserDTO> getAllUsers() {

		try {
			logger.info("DB URL: {}", dataSource.getConnection().getMetaData().getURL());
		} catch (Exception e) {
			logger.error("Failed to retrieve DB URL", e);
		}

		List<User> users = userRepo.findAll();
		logger.info("Retrieved {} users", users.size());
		List<UserDTO> userDTOs = new ArrayList<>();

		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			UserDTO dto = mapToDTO(user);
			userDTOs.add(dto);
		}

		return userDTOs;
	}

	@Override
	public UserDTO getUserById(Long id) {
		Optional<User> optionalUser = userRepo.findById(id);
		if (optionalUser.isPresent()) {
			return mapToDTO(optionalUser.get());
		}
		throw new ResourceNotFoundException("User not found with ID " + id);
	}

	@Override
	public UserDTO addUser(CreateUserRequest req) {
		if (userRepo.existsByEmail(req.getEmail())) {
			logger.warn("Attempt to create user with existing email: {}", req.getEmail());
			throw new ResourceExistsException("Email already exists!");
		}

		User user = new User();
		user.setName(req.getName());
		user.setEmail(req.getEmail());
		user.setPassword(passwordEncoder.encode(req.getPassword()));
		user.setRole(Role.CUSTOMER);

		User savedUser = userRepo.save(user);
		logger.info("Created new user with ID: {}", savedUser.getUserId());
		return mapToDTO(savedUser);
	}

	@Override
	public UserDTO updateUser(Long id, CreateUserRequest req) {
		Optional<User> optionalUser = userRepo.findById(id);
		if (!optionalUser.isPresent()) {
			throw new ResourceNotFoundException("User not found with ID " + id);
		}

		User user = optionalUser.get();
		user.setName(req.getName());
		user.setEmail(req.getEmail());
		user.setPassword(passwordEncoder.encode(req.getPassword()));
		logger.info("Updated user with ID: {}", id);
		return mapToDTO(user);
	}

	@Override
	public String deleteUser(Long id) {
		boolean exists = userRepo.existsById(id);
		if (!exists) {
			throw new ResourceNotFoundException("User not found with ID " + id);
		}

		userRepo.deleteById(id);
		logger.info("Deleted user with ID: {}", id);
		return "User with ID " + id + " deleted successfully!";
	}

	@Override
	@Async
	public void deleteAllUsers() {
		userRepo.deleteAll();
		logger.info("Deleted all users asynchronously");
	}
}
