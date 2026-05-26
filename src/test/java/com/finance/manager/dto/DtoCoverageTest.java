package com.finance.manager.dto;

import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.YearMonthAttributeConverter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DtoCoverageTest {
    @Test
    void coversSimpleResponseDtos() {
        ApiResponse<String> apiResponse = ApiResponse.success("Created", "ok");
        assertThat(apiResponse.isSuccess()).isTrue();
        assertThat(apiResponse.getMessage()).isEqualTo("Created");
        assertThat(apiResponse.getData()).isEqualTo("ok");
        assertThat(apiResponse.getTimestamp()).isBeforeOrEqualTo(Instant.now());
        assertThat(ApiResponse.success("ok").getData()).isEqualTo("ok");

        ErrorResponse error = new ErrorResponse("Bad Request", "Invalid", 400, "/api/test");
        error.setValidationErrors(Map.of("name", "required"));
        assertThat(error.isSuccess()).isFalse();
        assertThat(error.getError()).isEqualTo("Bad Request");
        assertThat(error.getMessage()).isEqualTo("Invalid");
        assertThat(error.getStatus()).isEqualTo(400);
        assertThat(error.getPath()).isEqualTo("/api/test");
        assertThat(error.getValidationErrors()).containsEntry("name", "required");

        PageResponse<String> page = new PageResponse<>(List.of("one"), 0, 10, 1, 1, true);
        assertThat(page.getContent()).containsExactly("one");
        assertThat(page.getPage()).isZero();
        assertThat(page.getSize()).isEqualTo(10);
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.isLast()).isTrue();

        DashboardSummaryResponse dashboard = new DashboardSummaryResponse(BigDecimal.TEN, BigDecimal.ONE, new BigDecimal("9"), List.of());
        assertThat(dashboard.getTotalIncome()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(dashboard.getTotalExpense()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(dashboard.getNetSavings()).isEqualByComparingTo("9");
        assertThat(dashboard.getRecentTransactions()).isEmpty();
    }

    @Test
    void coversRequestAndDomainDtos() {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Food");
        categoryRequest.setType(CategoryType.EXPENSE);
        assertThat(categoryRequest.getName()).isEqualTo("Food");
        assertThat(categoryRequest.getType()).isEqualTo(CategoryType.EXPENSE);

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(1L);
        categoryResponse.setName("Food");
        categoryResponse.setType(CategoryType.EXPENSE);
        categoryResponse.setCustom(true);
        assertThat(categoryResponse.getId()).isEqualTo(1L);
        assertThat(categoryResponse.getName()).isEqualTo("Food");
        assertThat(categoryResponse.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(categoryResponse.isCustom()).isTrue();

        CategoryTotal total = new CategoryTotal();
        total.setCategoryId(2L);
        total.setCategoryName("Salary");
        total.setTotal(BigDecimal.TEN);
        assertThat(total.getCategoryId()).isEqualTo(2L);
        assertThat(total.getCategoryName()).isEqualTo("Salary");
        assertThat(total.getTotal()).isEqualByComparingTo(BigDecimal.TEN);

        ReportResponse report = new ReportResponse();
        report.setTotalIncome(BigDecimal.TEN);
        report.setTotalExpense(BigDecimal.ONE);
        report.setNetBalance(new BigDecimal("9"));
        report.setExpensesByCategory(List.of(total));
        report.setIncomeByCategory(List.of(total));
        assertThat(report.getTotalIncome()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(report.getTotalExpense()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(report.getNetBalance()).isEqualByComparingTo("9");
        assertThat(report.getExpensesByCategory()).hasSize(1);
        assertThat(report.getIncomeByCategory()).hasSize(1);
    }

    @Test
    void coversMutableRequestDtosAndConverter() {
        LoginRequest login = new LoginRequest();
        login.setUsername("aakash@example.com");
        login.setPassword("Strong123");
        assertThat(login.getUsername()).isEqualTo("aakash@example.com");
        assertThat(login.getPassword()).isEqualTo("Strong123");

        RegisterRequest register = new RegisterRequest();
        register.setUsername("aakash@example.com");
        register.setPassword("Strong123");
        register.setFullName("Aakash");
        register.setPhoneNumber("9999999999");
        assertThat(register.getUsername()).isEqualTo("aakash@example.com");
        assertThat(register.getPassword()).isEqualTo("Strong123");
        assertThat(register.getFullName()).isEqualTo("Aakash");
        assertThat(register.getPhoneNumber()).isEqualTo("9999999999");

        TransactionRequest transaction = new TransactionRequest();
        transaction.setAmount(BigDecimal.TEN);
        transaction.setDate(LocalDate.now());
        transaction.setCategory("Food");
        transaction.setCategoryId(1L);
        transaction.setType(CategoryType.EXPENSE);
        transaction.setDescription("Lunch");
        assertThat(transaction.getAmount()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(transaction.getDate()).isEqualTo(LocalDate.now());
        assertThat(transaction.getCategory()).isEqualTo("Food");
        assertThat(transaction.getCategoryId()).isEqualTo(1L);
        assertThat(transaction.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(transaction.getDescription()).isEqualTo("Lunch");

        YearMonthAttributeConverter converter = new YearMonthAttributeConverter();
        assertThat(converter.convertToDatabaseColumn(YearMonth.of(2026, 5))).isEqualTo("2026-05");
        assertThat(converter.convertToEntityAttribute("2026-05")).isEqualTo(YearMonth.of(2026, 5));
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
