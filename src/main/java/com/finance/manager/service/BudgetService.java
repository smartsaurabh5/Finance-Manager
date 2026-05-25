package com.finance.manager.service;

import com.finance.manager.dto.BudgetRequest;
import com.finance.manager.dto.BudgetResponse;
import com.finance.manager.entity.Budget;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.exception.ForbiddenException;
import com.finance.manager.exception.ResourceNotFoundException;
import com.finance.manager.repository.BudgetRepository;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(BudgetRepository budgetRepository, CategoryRepository categoryRepository,
                         TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<BudgetResponse> getMonthlyBudgets(User user, int year, int month) {
        YearMonth yearMonth = validateMonth(year, month);
        return budgetRepository.findByUserAndMonth(user, yearMonth).stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public BudgetResponse upsertBudget(BudgetRequest request, User user) {
        YearMonth month = validateMonth(request.getYear(), request.getMonth());
        Category category = categoryRepository.findByIdAndUser(request.getCategoryId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (category.getType() != CategoryType.EXPENSE) {
            throw new IllegalArgumentException("Budgets can only be assigned to expense categories");
        }
        Budget budget = budgetRepository.findByUserAndCategoryAndMonth(user, category, month).orElseGet(Budget::new);
        budget.setUser(user);
        budget.setCategory(category);
        budget.setMonth(month);
        budget.setLimitAmount(request.getLimitAmount());
        return mapToResponse(budgetRepository.save(budget));
    }

    @Transactional
    public void deleteBudget(Long id, User user) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You don't have permission to access this budget");
        }
        budgetRepository.delete(budget);
    }

    private BudgetResponse mapToResponse(Budget budget) {
        BigDecimal spent = transactionRepository.findByUserAndCategoryAndDateBetweenOrderByDateDesc(
                        budget.getUser(), budget.getCategory(), budget.getMonth().atDay(1), budget.getMonth().atEndOfMonth())
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remaining = budget.getLimitAmount().subtract(spent);
        return new BudgetResponse(budget.getId(), budget.getMonth().getYear(), budget.getMonth().getMonthValue(),
                budget.getCategory().getId(), budget.getCategory().getName(), budget.getLimitAmount(), spent, remaining);
    }

    private YearMonth validateMonth(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (year < 2000 || year > YearMonth.now().getYear() + 1) {
            throw new IllegalArgumentException("Year must be between 2000 and " + (YearMonth.now().getYear() + 1));
        }
        return YearMonth.of(year, month);
    }
}
