package com.finance.manager.service;

import com.finance.manager.dto.SavingsGoalRequest;
import com.finance.manager.dto.SavingsGoalResponse;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.SavingsGoal;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.exception.ForbiddenException;
import com.finance.manager.repository.SavingsGoalRepository;
import com.finance.manager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingsGoalServiceTest {
    @Mock
    SavingsGoalRepository savingsGoalRepository;
    @Mock
    TransactionRepository transactionRepository;
    @InjectMocks
    SavingsGoalService savingsGoalService;

    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
    }

    @Test
    void createSavingsGoalDefaultsStartDateAndCalculatesProgress() {
        SavingsGoalRequest request = new SavingsGoalRequest();
        request.setGoalName("Emergency fund");
        request.setTargetAmount(new BigDecimal("1000.00"));
        request.setTargetDate(LocalDate.now().plusMonths(3));

        when(savingsGoalRepository.save(org.mockito.ArgumentMatchers.any(SavingsGoal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.findByUserAndDateBetweenOrderByDateDesc(
                org.mockito.ArgumentMatchers.eq(user),
                org.mockito.ArgumentMatchers.any(LocalDate.class),
                org.mockito.ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(List.of(transaction(CategoryType.INCOME, "700.00"), transaction(CategoryType.EXPENSE, "200.00")));

        SavingsGoalResponse response = savingsGoalService.createSavingsGoal(request, user);

        assertThat(response.getCurrentProgress()).isEqualByComparingTo("500.00");
        assertThat(response.getProgressPercentage()).isEqualByComparingTo("50.00");
    }

    @Test
    void createSavingsGoalRejectsPastTargetDate() {
        SavingsGoalRequest request = new SavingsGoalRequest();
        request.setGoalName("Old goal");
        request.setTargetAmount(BigDecimal.TEN);
        request.setTargetDate(LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> savingsGoalService.createSavingsGoal(request, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("future");
    }

    @Test
    void getSavingsGoalRejectsOtherUsersGoal() {
        User other = new User();
        other.setId(99L);
        SavingsGoal goal = goal(other);
        when(savingsGoalRepository.findById(10L)).thenReturn(Optional.of(goal));

        assertThatThrownBy(() -> savingsGoalService.getSavingsGoal(10L, user))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void updateSavingsGoalUpdatesAmountAndDate() {
        SavingsGoal goal = goal(user);
        SavingsGoalRequest request = new SavingsGoalRequest();
        request.setTargetAmount(new BigDecimal("2000.00"));
        request.setTargetDate(LocalDate.now().plusYears(1));
        when(savingsGoalRepository.findById(10L)).thenReturn(Optional.of(goal));
        when(savingsGoalRepository.save(goal)).thenReturn(goal);
        when(transactionRepository.findByUserAndDateBetweenOrderByDateDesc(user, goal.getStartDate(), LocalDate.now()))
                .thenReturn(List.of());

        SavingsGoalResponse response = savingsGoalService.updateSavingsGoal(10L, request, user);

        assertThat(response.getTargetAmount()).isEqualByComparingTo("2000.00");
    }

    private SavingsGoal goal(User owner) {
        SavingsGoal goal = new SavingsGoal();
        goal.setId(10L);
        goal.setUser(owner);
        goal.setGoalName("Emergency fund");
        goal.setTargetAmount(new BigDecimal("1000.00"));
        goal.setTargetDate(LocalDate.now().plusMonths(3));
        goal.setStartDate(LocalDate.now().minusMonths(1));
        return goal;
    }

    private Transaction transaction(CategoryType type, String amount) {
        Category category = Category.builder().name(type.name()).type(type).user(user).build();
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(new BigDecimal(amount));
        transaction.setDate(LocalDate.now());
        return transaction;
    }
}
