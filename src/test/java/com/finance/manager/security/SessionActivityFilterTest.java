package com.finance.manager.security;

import com.finance.manager.entity.User;
import com.finance.manager.service.AuthService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SessionActivityFilterTest {
    @Mock
    AuthService authService;
    @Mock
    FilterChain filterChain;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void recordsActivityForAuthenticatedCustomUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("aakash@example.com");
        user.setPassword("encoded");
        user.setFullName("Aakash");
        CustomUserDetails details = new CustomUserDetails(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities()));

        new SessionActivityFilter(authService).doFilter(
                new MockHttpServletRequest(), new MockHttpServletResponse(), filterChain);

        verify(authService).recordActivity(user);
        verify(filterChain).doFilter(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}
