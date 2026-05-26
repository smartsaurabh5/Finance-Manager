package com.finance.manager.security;

import com.finance.manager.service.AuthService;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

@Component
public class SessionLifecycleListener implements HttpSessionListener {
    private final AuthService authService;

    public SessionLifecycleListener(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        Object context = event.getSession().getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
        );
        if (context instanceof SecurityContext securityContext) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
                authService.recordLogout(userDetails.getUser());
            }
        }
    }
}
