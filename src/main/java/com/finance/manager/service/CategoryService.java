package com.finance.manager.service;

import com.finance.manager.dto.CategoryRequest;
import com.finance.manager.dto.CategoryResponse;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.User;
import com.finance.manager.exception.ConflictException;
import com.finance.manager.exception.ForbiddenException;
import com.finance.manager.exception.ResourceNotFoundException;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public CategoryService(CategoryRepository categoryRepository, TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository; 
    }

    public List<CategoryResponse> getAllCategories(User user) {
        return categoryRepository.findByUser(user).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request, User user) {
        if (categoryRepository.existsByNameIgnoreCaseAndUser(request.getName(), user)) {
            throw new ConflictException("Category with this name already exists");
        }
        Category category = new Category();
        category.setName(request.getName());
        category.setType(request.getType());
        category.setIsDefault(false);
        category.setUser(user);
        return mapToResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(String name, User user) {
        Category category = categoryRepository.findByNameIgnoreCaseAndUser(name, user)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (!category.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You don't have permission to access this category");
        }
        if (category.isDefault()) {
            throw new IllegalArgumentException("Cannot delete default categories");
        }
        if (transactionRepository.existsByCategory(category)) {
            throw new IllegalArgumentException("Cannot delete category used in transactions");
        }
        categoryRepository.delete(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getType(), !category.isDefault());
    }
}
