package com.finance.manager.controller;

import com.finance.manager.dto.CategoryRequest;
import com.finance.manager.dto.CategoryResponse;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.User;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {
    @Mock
    CategoryService categoryService;
    @InjectMocks
    CategoryController categoryController;

    CustomUserDetails userDetails;
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("aakash@example.com");
        user.setPassword("encoded");
        user.setFullName("Aakash");
        userDetails = new CustomUserDetails(user);
    }

    @Test
    void getCategoriesReturnsUserCategories() {
        List<CategoryResponse> categories = List.of(new CategoryResponse(1L, "Food", CategoryType.EXPENSE, false));
        when(categoryService.getAllCategories(user)).thenReturn(categories);

        var response = categoryController.getCategories(userDetails);

        assertThat(response.getBody()).isEqualTo(Map.of("categories", categories));
    }

    @Test
    void createCategoryReturnsCreated() {
        CategoryRequest request = new CategoryRequest("Travel", CategoryType.EXPENSE);
        CategoryResponse body = new CategoryResponse(2L, "Travel", CategoryType.EXPENSE, true);
        when(categoryService.createCategory(request, user)).thenReturn(body);

        var response = categoryController.createCategory(request, userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(body);
    }

    @Test
    void deleteCategoryReturnsMessage() {
        var response = categoryController.deleteCategory("Travel", userDetails);

        verify(categoryService).deleteCategory("Travel", user);
        assertThat(response.getBody()).isEqualTo(Map.of("message", "Category deleted successfully"));
    }
}
