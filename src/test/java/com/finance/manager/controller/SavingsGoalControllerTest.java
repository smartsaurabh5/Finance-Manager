package com.finance.manager.controller;

import com.finance.manager.dto.SavingsGoalRequest;
import com.finance.manager.dto.SavingsGoalResponse;
import com.finance.manager.entity.User;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.SavingsGoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingsGoalControllerTest {
    @Mock
    SavingsGoalService savingsGoalService;
    @InjectMocks
    SavingsGoalController savingsGoalController;

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
    void getSavingsGoalsReturnsWrapper() {
        when(savingsGoalService.getSavingsGoals(user)).thenReturn(List.of(goal()));

        var response = savingsGoalController.getSavingsGoals(userDetails);

        assertThat(response.getBody().get("goals")).hasSize(1);
    }

    @Test
    void createSavingsGoalReturnsCreated() {
        SavingsGoalRequest request = new SavingsGoalRequest();
        when(savingsGoalService.createSavingsGoal(request, user)).thenReturn(goal());

        var response = savingsGoalController.createSavingsGoal(request, userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void deleteSavingsGoalReturnsMessage() {
        var response = savingsGoalController.deleteSavingsGoal(4L, userDetails);

        verify(savingsGoalService).deleteSavingsGoal(4L, user);
        assertThat(response.getBody()).isEqualTo(Map.of("message", "Goal deleted successfully"));
    }

    private SavingsGoalResponse goal() {
        return new SavingsGoalResponse(1L, "Emergency fund", new BigDecimal("1000.00"),
                LocalDate.now().plusMonths(3), LocalDate.now(), BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("1000.00"));
    }
}
