package com.finance.manager.config;

import com.finance.manager.security.CustomUserDetailsService;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.security.JwtAuthenticationFilter;
import com.finance.manager.security.SessionActivityFilter;
import com.finance.manager.service.AuthService;
import com.finance.manager.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SecurityConfigTest {

    @Test
    void logoutSuccessHandlerReturnsJsonOk() throws Exception {
        SecurityConfig securityConfig = new SecurityConfig(
                mock(CustomUserDetailsService.class),
                mock(SessionActivityFilter.class),
                mock(JwtAuthenticationFilter.class),
                mock(AuthService.class),
                List.of("https://finance.example.com")
        );
        MockHttpServletResponse response = new MockHttpServletResponse();

        securityConfig.logoutSuccessHandler().onLogoutSuccess(
                new MockHttpServletRequest(),
                response,
                null
        );

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(response.getContentAsString()).isEqualTo("{\"message\":\"Logout successful\"}");
    }

    @Test
    void logoutHandlerRecordsLogoutForAuthenticatedUser() {
        AuthService authService = mock(AuthService.class);
        SecurityConfig securityConfig = new SecurityConfig(
                mock(CustomUserDetailsService.class),
                mock(SessionActivityFilter.class),
                mock(JwtAuthenticationFilter.class),
                authService,
                List.of("https://finance.example.com")
        );
        User user = new User();
        user.setId(1L);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        securityConfig.logoutHandler().logout(
                new MockHttpServletRequest(),
                new MockHttpServletResponse(),
                authentication
        );

        verify(authService).recordLogout(user);
    }
}
