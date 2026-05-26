package com.finance.manager.controller;

import com.finance.manager.dto.LoginRequest;
import com.finance.manager.dto.LoginResponse;
import com.finance.manager.dto.RegisterRequest;
import com.finance.manager.entity.User;
import com.finance.manager.exception.AccountLockedException;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.security.JwtService;
import com.finance.manager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully", "userId", user.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException ex) {
            authService.recordLoginFailure(request.getUsername());
            throw ex;
        } catch (LockedException ex) {
            throw new AccountLockedException("Account is temporarily locked because of too many failed login attempts");
        }

        authService.recordLoginSuccess(request.getUsername());
        String token = jwtService.generateToken((CustomUserDetails) authentication.getPrincipal(),
                Boolean.TRUE.equals(request.getRememberMe()));
        long expiresAt = java.time.Instant.now().getEpochSecond()
                + jwtService.getExpiresInSeconds(Boolean.TRUE.equals(request.getRememberMe()));

        return ResponseEntity.ok(new LoginResponse("Login successful", "Bearer", token, expiresAt));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            authService.recordLogout(userDetails.getUser());
        }
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }
}
