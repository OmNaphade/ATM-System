package com.omnaphade.dtos;

import com.omnaphade.entites.Role;
import jakarta.transaction.Transactional;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Transactional
public class UserDTO {
	private Long userId;
	private String name;
	private String email;
	private Role role;
}
