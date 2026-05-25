package com.finance.manager.service;

import com.finance.manager.dto.RegisterRequest;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.User;
import com.finance.manager.exception.ConflictException;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, CategoryRepository categoryRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username (Email) is already taken");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);

        createDefaultCategories(savedUser);
        return savedUser;
    }

    private void createDefaultCategories(User user) {
        List<Category> defaultCategories = List.of(
                Category.builder().name("Salary").type(CategoryType.INCOME).isDefault(true).user(user).build(),
                Category.builder().name("Food").type(CategoryType.EXPENSE).isDefault(true).user(user).build(),
                Category.builder().name("Rent").type(CategoryType.EXPENSE).isDefault(true).user(user).build(),
                Category.builder().name("Transportation").type(CategoryType.EXPENSE).isDefault(true).user(user).build(),
                Category.builder().name("Entertainment").type(CategoryType.EXPENSE).isDefault(true).user(user).build(),
                Category.builder().name("Healthcare").type(CategoryType.EXPENSE).isDefault(true).user(user).build(),
                Category.builder().name("Utilities").type(CategoryType.EXPENSE).isDefault(true).user(user).build()
        );
        categoryRepository.saveAll(defaultCategories);
    }
}
