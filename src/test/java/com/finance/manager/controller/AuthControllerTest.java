package com.finance.manager.controller;

import com.finance.manager.dto.LoginRequest;
import com.finance.manager.dto.RegisterRequest;
import com.finance.manager.entity.User;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.security.JwtService;
import com.finance.manager.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    AuthService authService;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    JwtService jwtService;
    @Mock
    Authentication authentication;
    @InjectMocks
    AuthController authController;

    @Test
    void registerReturnsCreatedUserId() {
        RegisterRequest request = new RegisterRequest();
        User user = new User();
        user.setId(11L);
        when(authService.registerUser(request)).thenReturn(user);

        var response = authController.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsEntry("userId", 11L);
    }

    @Test
    void loginReturnsJwtToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("aakash@example.com");
        request.setPassword("Strong123");
        request.setRememberMe(true);
        User user = new User();
        user.setUsername("aakash@example.com");
        user.setPassword("encoded");
        user.setFullName("Aakash");
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails, true)).thenReturn("jwt-token");
        when(jwtService.getExpiresInSeconds(true)).thenReturn(604800L);

        var response = authController.login(request);

        assertThat(response.getBody().getMessage()).isEqualTo("Login successful");
        assertThat(response.getBody().getTokenType()).isEqualTo("Bearer");
        assertThat(response.getBody().getAccessToken()).isEqualTo("jwt-token");
        verify(authService).recordLoginSuccess("aakash@example.com");
    }

    @Test
    void logoutRecordsAuthenticatedUserLogout() {
        User user = new User();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        var response = authController.logout(userDetails);

        assertThat(response.getBody()).isEqualTo(Map.of("message", "Logout successful"));
        verify(authService).recordLogout(user);
    }
}
