package com.finance.manager.security;

import com.finance.manager.entity.User;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validBearerTokenAuthenticatesRequest() throws Exception {
        JwtService jwtService = new JwtService("test-secret-test-secret-test-secret", 1800, 604800);
        CustomUserDetailsService userDetailsService = mock(CustomUserDetailsService.class);
        CustomUserDetails userDetails = userDetails("aakash@example.com");
        String token = jwtService.generateToken(userDetails, false);
        when(userDetailsService.loadUserByUsername("aakash@example.com")).thenReturn(userDetails);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("aakash@example.com");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void missingBearerTokenLeavesRequestUnauthenticated() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                new JwtService("test-secret-test-secret-test-secret", 1800, 604800),
                mock(CustomUserDetailsService.class)
        );
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    private CustomUserDetails userDetails(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("encoded");
        user.setFullName("Aakash");
        return new CustomUserDetails(user);
    }
}
