package com.finance.manager.service;

import com.finance.manager.dto.BudgetRequest;
import com.finance.manager.dto.BudgetResponse;
import com.finance.manager.entity.Budget;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.exception.ForbiddenException;
import com.finance.manager.repository.BudgetRepository;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {
    @Mock
    BudgetRepository budgetRepository;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    TransactionRepository transactionRepository;
    @InjectMocks
    BudgetService budgetService;

    User user;
    Category food;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        food = Category.builder().id(2L).name("Food").type(CategoryType.EXPENSE).user(user).build();
    }

    @Test
    void upsertBudgetCreatesExpenseBudgetAndCalculatesRemainingAmount() {
        BudgetRequest request = new BudgetRequest();
        request.setCategoryId(2L);
        request.setYear(2026);
        request.setMonth(5);
        request.setLimitAmount(new BigDecimal("1000.00"));

        when(categoryRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(food));
        when(budgetRepository.findByUserAndCategoryAndMonth(user, food, YearMonth.of(2026, 5))).thenReturn(Optional.empty());
        when(budgetRepository.save(org.mockito.ArgumentMatchers.any(Budget.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.findByUserAndCategoryAndDateBetweenOrderByDateDesc(user, food,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31)))
                .thenReturn(List.of(transaction(new BigDecimal("250.00"))));

        BudgetResponse response = budgetService.upsertBudget(request, user);

        assertThat(response.getSpentAmount()).isEqualByComparingTo("250.00");
        assertThat(response.getRemainingAmount()).isEqualByComparingTo("750.00");
    }

    @Test
    void upsertBudgetRejectsIncomeCategory() {
        BudgetRequest request = new BudgetRequest();
        request.setCategoryId(3L);
        request.setYear(2026);
        request.setMonth(5);
        request.setLimitAmount(BigDecimal.TEN);
        Category salary = Category.builder().id(3L).name("Salary").type(CategoryType.INCOME).user(user).build();
        when(categoryRepository.findByIdAndUser(3L, user)).thenReturn(Optional.of(salary));

        assertThatThrownBy(() -> budgetService.upsertBudget(request, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("expense");
    }

    @Test
    void deleteBudgetRejectsOtherUsersBudget() {
        User other = new User();
        other.setId(99L);
        Budget budget = budget(other);
        when(budgetRepository.findById(5L)).thenReturn(Optional.of(budget));

        assertThatThrownBy(() -> budgetService.deleteBudget(5L, user))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void deleteBudgetRemovesOwnedBudget() {
        Budget budget = budget(user);
        when(budgetRepository.findById(5L)).thenReturn(Optional.of(budget));

        budgetService.deleteBudget(5L, user);

        verify(budgetRepository).delete(budget);
    }

    private Budget budget(User owner) {
        Budget budget = new Budget();
        budget.setUser(owner);
        budget.setCategory(food);
        budget.setMonth(YearMonth.of(2026, 5));
        budget.setLimitAmount(BigDecimal.TEN);
        return budget;
    }

    private Transaction transaction(BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(food);
        transaction.setAmount(amount);
        transaction.setDate(LocalDate.of(2026, 5, 5));
        return transaction;
    }
}
