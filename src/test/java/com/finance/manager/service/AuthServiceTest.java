package com.finance.manager.service;

import com.finance.manager.dto.RegisterRequest;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.User;
import com.finance.manager.exception.ConflictException;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    AuthService authService;

    @Test
    void registerUserEncodesPasswordAndCreatesDefaultCategories() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("aakash@example.com");
        request.setPassword("Strong123");
        request.setFullName("Aakash");
        request.setPhoneNumber("9999999999");

        when(userRepository.existsByUsername("aakash@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Strong123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(10L);
            return user;
        });

        User saved = authService.registerUser(request);

        assertThat(saved.getPassword()).isEqualTo("encoded");
        ArgumentCaptor<List<Category>> captor = ArgumentCaptor.forClass(List.class);
        verify(categoryRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(7);
        assertThat(captor.getValue()).allMatch(Category::isDefault);
    }

    @Test
    void registerUserRejectsDuplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("aakash@example.com");
        when(userRepository.existsByUsername("aakash@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.registerUser(request))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void failedLoginLocksAccountAfterFiveAttempts() {
        User user = new User();
        user.setUsername("aakash@example.com");
        user.setFailedLoginAttempts(4);
        when(userRepository.findByUsername("aakash@example.com")).thenReturn(Optional.of(user));

        authService.recordLoginFailure("aakash@example.com");

        assertThat(user.getFailedLoginAttempts()).isEqualTo(5);
        assertThat(user.getLockedUntil()).isNotNull();
        verify(userRepository).save(user);
    }
}
