package com.finance.manager.controller;

import com.finance.manager.dto.PageResponse;
import com.finance.manager.dto.TransactionRequest;
import com.finance.manager.dto.TransactionResponse;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.User;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {
    @Mock
    TransactionService transactionService;
    @InjectMocks
    TransactionController transactionController;

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
    void getTransactionsReturnsPaginationMetadata() {
        TransactionResponse transaction = transaction();
        when(transactionService.getTransactions(org.mockito.ArgumentMatchers.eq(user),
                org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.eq(CategoryType.EXPENSE),
                any(Pageable.class)))
                .thenReturn(new PageResponse<>(List.of(transaction), 0, 20, 1, 1, true));

        var response = transactionController.getTransactions(null, null, null, CategoryType.EXPENSE, 0, 20, userDetails);

        assertThat(response.getBody()).containsEntry("totalElements", 1L);
        assertThat(response.getBody()).containsEntry("last", true);
    }

    @Test
    void createTransactionReturnsCreated() {
        TransactionRequest request = new TransactionRequest();
        TransactionResponse body = transaction();
        when(transactionService.createTransaction(request, user)).thenReturn(body);

        var response = transactionController.createTransaction(request, userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(body);
    }

    @Test
    void recentTransactionsReturnsWrapper() {
        when(transactionService.getRecentTransactions(user)).thenReturn(List.of(transaction()));

        var response = transactionController.getRecentTransactions(userDetails);

        assertThat(response.getBody().get("transactions")).hasSize(1);
    }

    @Test
    void deleteTransactionReturnsMessage() {
        var response = transactionController.deleteTransaction(9L, userDetails);

        verify(transactionService).deleteTransaction(9L, user);
        assertThat(response.getBody()).isEqualTo(Map.of("message", "Transaction deleted successfully"));
    }

    private TransactionResponse transaction() {
        return new TransactionResponse(1L, BigDecimal.TEN, LocalDate.now(), "Food", "Lunch", CategoryType.EXPENSE);
    }
}
