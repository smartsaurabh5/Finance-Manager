package com.finance.manager.controller;

import com.finance.manager.dto.DashboardSummaryResponse;
import com.finance.manager.entity.User;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {
    @Mock
    ReportService reportService;
    @InjectMocks
    DashboardController dashboardController;

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
    void getSummaryReturnsDashboardSummary() {
        DashboardSummaryResponse summary = new DashboardSummaryResponse(BigDecimal.TEN, BigDecimal.ONE, new BigDecimal("9"), List.of());
        when(reportService.getDashboardSummary(user)).thenReturn(summary);

        var response = dashboardController.getSummary(userDetails);

        assertThat(response.getBody()).isEqualTo(summary);
    }
}
