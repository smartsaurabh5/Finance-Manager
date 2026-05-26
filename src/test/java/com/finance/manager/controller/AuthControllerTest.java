package com.finance.manager.controller;

import com.finance.manager.dto.LoginRequest;
import com.finance.manager.dto.RegisterRequest;
import com.finance.manager.entity.User;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

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
    void loginReturnsSessionSuccessMessage() {
        LoginRequest request = new LoginRequest();
        request.setUsername("aakash@example.com");
        request.setPassword("Strong123");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        var response = authController.login(request, httpRequest);

        assertThat(response.getBody()).isEqualTo(Map.of("message", "Login successful"));
        assertThat(httpRequest.getSession().getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
        )).isNotNull();
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
