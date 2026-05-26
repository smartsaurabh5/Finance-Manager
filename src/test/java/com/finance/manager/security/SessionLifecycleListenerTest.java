package com.finance.manager.security;

import com.finance.manager.entity.User;
import com.finance.manager.service.AuthService;
import jakarta.servlet.http.HttpSessionEvent;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class SessionLifecycleListenerTest {

    @Test
    void sessionDestroyedRecordsLogoutForAuthenticatedUser() {
        AuthService authService = mock(AuthService.class);
        SessionLifecycleListener listener = new SessionLifecycleListener(authService);
        User user = new User();
        user.setId(1L);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        SecurityContext securityContext = new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        listener.sessionDestroyed(new HttpSessionEvent(session));

        verify(authService).recordLogout(user);
    }

    @Test
    void sessionDestroyedIgnoresAnonymousSession() {
        AuthService authService = mock(AuthService.class);
        SessionLifecycleListener listener = new SessionLifecycleListener(authService);

        listener.sessionDestroyed(new HttpSessionEvent(new MockHttpSession()));

        verifyNoInteractions(authService);
    }
}
