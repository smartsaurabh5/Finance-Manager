package com.finance.manager.security;

import com.finance.manager.entity.User;
import com.finance.manager.entity.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {
    @Test
    void exposesRoleAuthorityAndLockState() {
        User user = new User();
        user.setUsername("aakash@example.com");
        user.setPassword("encoded");
        user.setFullName("Aakash");
        user.setRole(UserRole.ADMIN);
        user.setLockedUntil(LocalDateTime.now().plusMinutes(5));

        CustomUserDetails details = new CustomUserDetails(user);

        assertThat(details.getAuthorities()).extracting(Object::toString).contains("ROLE_ADMIN");
        assertThat(details.isAccountNonLocked()).isFalse();
        assertThat(details.isAccountNonExpired()).isTrue();
        assertThat(details.isCredentialsNonExpired()).isTrue();
        assertThat(details.isEnabled()).isTrue();
    }
}
