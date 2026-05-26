package com.finance.manager.security;

import com.finance.manager.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    @Test
    void generatedTokenContainsUsernameAndValidSignature() {
        JwtService jwtService = new JwtService("test-secret-test-secret-test-secret", 1800, 604800);
        CustomUserDetails userDetails = userDetails("aakash@example.com");

        String token = jwtService.generateToken(userDetails, false);

        assertThat(jwtService.extractUsername(token)).isEqualTo("aakash@example.com");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
        assertThat(jwtService.getExpiresInSeconds(false)).isEqualTo(1800);
        assertThat(jwtService.getExpiresInSeconds(true)).isEqualTo(604800);
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtService jwtService = new JwtService("test-secret-test-secret-test-secret", 1800, 604800);
        String token = jwtService.generateToken(userDetails("aakash@example.com"), false);
        String tamperedToken = token.substring(0, token.length() - 2) + "xx";

        assertThatThrownBy(() -> jwtService.extractUsername(tamperedToken))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private CustomUserDetails userDetails(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("encoded");
        user.setFullName("Aakash");
        return new CustomUserDetails(user);
    }
}
