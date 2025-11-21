package com.resameet.presentation.dto;

import com.resameet.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private Set<Role> roles;
}
