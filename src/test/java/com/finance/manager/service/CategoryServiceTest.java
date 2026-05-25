package com.finance.manager.service;

import com.finance.manager.dto.CategoryRequest;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.User;
import com.finance.manager.exception.ConflictException;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    TransactionRepository transactionRepository;
    @InjectMocks
    CategoryService categoryService;

    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
    }

    @Test
    void createCategoryRejectsDuplicateNameForUser() {
        CategoryRequest request = new CategoryRequest("Food", CategoryType.EXPENSE);
        when(categoryRepository.existsByNameIgnoreCaseAndUser("Food", user)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(request, user))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void deleteCategoryRejectsCategoryLinkedToTransactions() {
        Category category = Category.builder().id(2L).name("Travel").type(CategoryType.EXPENSE).isDefault(false).user(user).build();
        when(categoryRepository.findByNameIgnoreCaseAndUser("Travel", user)).thenReturn(Optional.of(category));
        when(transactionRepository.existsByCategory(category)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory("Travel", user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("transactions");
    }

    @Test
    void getAllCategoriesMapsDefaultAsNotCustom() {
        Category salary = Category.builder().id(1L).name("Salary").type(CategoryType.INCOME).isDefault(true).user(user).build();
        when(categoryRepository.findByUser(user)).thenReturn(List.of(salary));

        var categories = categoryService.getAllCategories(user);

        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).isCustom()).isFalse();
    }

    @Test
    void createCategorySavesCustomCategory() {
        CategoryRequest request = new CategoryRequest("Travel", CategoryType.EXPENSE);
        when(categoryRepository.existsByNameIgnoreCaseAndUser("Travel", user)).thenReturn(false);
        when(categoryRepository.save(org.mockito.ArgumentMatchers.any(Category.class)))
                .thenAnswer(invocation -> {
                    Category category = invocation.getArgument(0);
                    category.setId(3L);
                    return category;
                });

        var response = categoryService.createCategory(request, user);

        assertThat(response.isCustom()).isTrue();
        assertThat(response.getName()).isEqualTo("Travel");
    }

    @Test
    void deleteCategoryRejectsDefaultCategory() {
        Category category = Category.builder().id(2L).name("Food").type(CategoryType.EXPENSE).isDefault(true).user(user).build();
        when(categoryRepository.findByNameIgnoreCaseAndUser("Food", user)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> categoryService.deleteCategory("Food", user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("default");
    }
}
