package com.resameet.presentation.controller;

import com.resameet.application.service.AuthService;
import com.resameet.domain.model.Role;
import com.resameet.domain.model.User;
import com.resameet.infrastructure.security.JwtUtil;
import com.resameet.presentation.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthService authService;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testLogin_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setRoles(Set.of(Role.USER));
        
        when(authService.authenticate("user", "pass")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("user")).thenReturn("test-token");
        
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("pass");
        
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.username").value("user"));
    }
}





