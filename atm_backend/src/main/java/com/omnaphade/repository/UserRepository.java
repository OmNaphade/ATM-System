package com.omnaphade.repository;

import com.omnaphade.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	
	boolean existsByEmail(String email);
}
