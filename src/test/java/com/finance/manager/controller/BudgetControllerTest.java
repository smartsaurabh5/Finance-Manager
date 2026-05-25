package com.finance.manager.controller;

import com.finance.manager.dto.BudgetRequest;
import com.finance.manager.dto.BudgetResponse;
import com.finance.manager.entity.User;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {
    @Mock
    BudgetService budgetService;
    @InjectMocks
    BudgetController budgetController;

    User user;
    CustomUserDetails userDetails;

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
    void getBudgetsReturnsWrapper() {
        when(budgetService.getMonthlyBudgets(user, 2026, 5)).thenReturn(List.of(budget()));

        var response = budgetController.getBudgets(2026, 5, userDetails);

        assertThat(response.getBody().get("budgets")).hasSize(1);
    }

    @Test
    void upsertBudgetReturnsCreated() {
        BudgetRequest request = new BudgetRequest();
        when(budgetService.upsertBudget(request, user)).thenReturn(budget());

        var response = budgetController.upsertBudget(request, userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void deleteBudgetReturnsMessage() {
        var response = budgetController.deleteBudget(4L, userDetails);

        verify(budgetService).deleteBudget(4L, user);
        assertThat(response.getBody()).isEqualTo(Map.of("message", "Budget deleted successfully"));
    }

    private BudgetResponse budget() {
        return new BudgetResponse(1L, 2026, 5, 2L, "Food",
                new BigDecimal("1000.00"), BigDecimal.ZERO, new BigDecimal("1000.00"));
    }
}
