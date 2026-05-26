package com.finance.manager.controller;

import com.finance.manager.dto.CategoryRequest;
import com.finance.manager.dto.CategoryResponse;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<Map<String, List<CategoryResponse>>> getCategories(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(Map.of("categories", categoryService.getAllCategories(userDetails.getUser())));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(request, userDetails.getUser()));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable String name,
                                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        categoryService.deleteCategory(name, userDetails.getUser());
        return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
    }
}
