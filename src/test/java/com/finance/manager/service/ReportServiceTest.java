package com.finance.manager.service;

import com.finance.manager.entity.Category;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @Mock
    TransactionRepository transactionRepository;
    @InjectMocks
    ReportService reportService;

    @Test
    void monthlyReportValidatesMonth() {
        assertThatThrownBy(() -> reportService.getMonthlyReport(new User(), 2025, 13))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void yearlyReportCalculatesNetSavings() {
        User user = new User();
        Category salary = Category.builder().name("Salary").type(CategoryType.INCOME).user(user).build();
        Category food = Category.builder().name("Food").type(CategoryType.EXPENSE).user(user).build();
        Transaction income = transaction(salary, "1000.00");
        Transaction expense = transaction(food, "250.00");
        when(transactionRepository.findByUserAndDateBetweenOrderByDateDesc(user,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)))
                .thenReturn(List.of(income, expense));

        Map<String, Object> report = reportService.getYearlyReport(user, 2025);

        assertThat((BigDecimal) report.get("netSavings")).isEqualByComparingTo(new BigDecimal("750.00"));
    }

    @Test
    void exportMonthlyCsvIncludesRows() {
        User user = new User();
        Category food = Category.builder().name("Food").type(CategoryType.EXPENSE).user(user).build();
        Transaction expense = transaction(food, "25.00");
        expense.setDescription("Lunch");
        when(transactionRepository.findByUserAndDateBetweenOrderByDateDesc(user,
                LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31)))
                .thenReturn(List.of(expense));

        String csv = reportService.exportMonthlyCsv(user, 2025, 5);

        assertThat(csv).contains("id,date,type,category,amount,description");
        assertThat(csv).contains("EXPENSE");
        assertThat(csv).contains("\"Lunch\"");
    }

    @Test
    void dashboardSummaryUsesCurrentMonthAndRecentTransactions() {
        User user = new User();
        Category salary = Category.builder().name("Salary").type(CategoryType.INCOME).user(user).build();
        Category food = Category.builder().name("Food").type(CategoryType.EXPENSE).user(user).build();
        Transaction income = transaction(salary, "1000.00");
        Transaction expense = transaction(food, "300.00");
        when(transactionRepository.findByUserAndDateBetweenOrderByDateDesc(
                org.mockito.ArgumentMatchers.eq(user),
                org.mockito.ArgumentMatchers.any(LocalDate.class),
                org.mockito.ArgumentMatchers.any(LocalDate.class)))
                .thenReturn(List.of(income, expense));
        when(transactionRepository.findTop5ByUserOrderByDateDesc(user)).thenReturn(List.of(expense));

        var summary = reportService.getDashboardSummary(user);

        assertThat(summary.getTotalIncome()).isEqualByComparingTo("1000.00");
        assertThat(summary.getTotalExpense()).isEqualByComparingTo("300.00");
        assertThat(summary.getRecentTransactions()).hasSize(1);
    }

    private Transaction transaction(Category category, String amount) {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setUser(category.getUser());
        transaction.setCategory(category);
        transaction.setAmount(new BigDecimal(amount));
        transaction.setDate(LocalDate.of(2025, 5, 1));
        return transaction;
    }
}
