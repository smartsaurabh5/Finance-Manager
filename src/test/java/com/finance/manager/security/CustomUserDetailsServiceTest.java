package com.finance.manager.security;

import com.finance.manager.entity.User;
import com.finance.manager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsernameReturnsDetails() {
        User user = new User();
        user.setUsername("aakash@example.com");
        user.setPassword("encoded");
        user.setFullName("Aakash");
        when(userRepository.findByUsername("aakash@example.com")).thenReturn(Optional.of(user));

        var details = customUserDetailsService.loadUserByUsername("aakash@example.com");

        assertThat(details.getUsername()).isEqualTo("aakash@example.com");
    }

    @Test
    void loadUserByUsernameThrowsWhenMissing() {
        when(userRepository.findByUsername("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("missing@example.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
