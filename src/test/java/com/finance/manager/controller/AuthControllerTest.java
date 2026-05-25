package com.finance.manager.controller;

import com.finance.manager.dto.LoginRequest;
import com.finance.manager.dto.RegisterRequest;
import com.finance.manager.entity.User;
import com.finance.manager.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

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
    SessionAuthenticationStrategy sessionAuthenticationStrategy;
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
    void loginStoresSecurityContextInSession() {
        LoginRequest request = new LoginRequest();
        request.setUsername("aakash@example.com");
        request.setPassword("Strong123");
        request.setRememberMe(true);
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        var response = authController.login(request, servletRequest, servletResponse);

        assertThat(response.getBody()).isEqualTo(Map.of("message", "Login successful"));
        assertThat(servletRequest.getSession().getMaxInactiveInterval()).isEqualTo(7 * 24 * 60 * 60);
        verify(sessionAuthenticationStrategy).onAuthentication(authentication, servletRequest, servletResponse);
        verify(authService).recordLoginSuccess("aakash@example.com");
    }

    @Test
    void logoutInvalidatesExistingSession() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true);

        var response = authController.logout(request, new MockHttpServletResponse());

        assertThat(response.getBody()).isEqualTo(Map.of("message", "Logout successful"));
    }
}
