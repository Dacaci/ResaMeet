package com.resameet.presentation.controller;

import com.resameet.application.service.AuthService;
import com.resameet.domain.model.User;
import com.resameet.infrastructure.security.JwtUtil;
import com.resameet.presentation.dto.LoginRequest;
import com.resameet.presentation.dto.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.authenticate(request.getUsername(), request.getPassword())
            .orElseThrow(() -> new RuntimeException("Identifiants invalides"));

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getRoles()));
    }
}
