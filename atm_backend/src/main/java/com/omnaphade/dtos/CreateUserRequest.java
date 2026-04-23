package com.omnaphade.dtos;

import jakarta.transaction.Transactional;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Transactional
public class CreateUserRequest {
    private String name;
    private String email;
    private String password;
}
