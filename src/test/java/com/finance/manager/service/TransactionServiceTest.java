package com.finance.manager.service;

import com.finance.manager.dto.TransactionRequest;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.exception.ForbiddenException;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    TransactionRepository transactionRepository;
    @Mock
    CategoryRepository categoryRepository;
    @InjectMocks
    TransactionService transactionService;

    User user;
    Category food;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        food = Category.builder().id(2L).name("Food").type(CategoryType.EXPENSE).user(user).build();
    }

    @Test
    void createTransactionRejectsFutureDate() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("10.00"));
        request.setDate(LocalDate.now().plusDays(1));
        request.setCategoryId(2L);

        assertThatThrownBy(() -> transactionService.createTransaction(request, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("future");
    }

    @Test
    void createTransactionSavesWhenCategoryBelongsToUser() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("10.00"));
        request.setDate(LocalDate.now());
        request.setCategoryId(2L);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(food));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        transactionService.createTransaction(request, user);

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void getTransactionRejectsOtherUsersData() {
        User other = new User();
        other.setId(99L);
        Transaction transaction = new Transaction();
        transaction.setId(5L);
        transaction.setUser(other);
        transaction.setCategory(food);
        transaction.setAmount(BigDecimal.ONE);
        transaction.setDate(LocalDate.now());
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(transaction));

        assertThatThrownBy(() -> transactionService.getTransaction(5L, user))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void getTransactionsRejectsPartialDateRange() {
        assertThatThrownBy(() -> transactionService.getTransactions(user, LocalDate.now(), null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Both startDate and endDate");
    }

    @Test
    void getTransactionsRejectsStartDateAfterEndDate() {
        assertThatThrownBy(() -> transactionService.getTransactions(user, LocalDate.now(), LocalDate.now().minusDays(1), null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("startDate");
    }

    @Test
    void getTransactionsPaginatesByCategoryAndDateRange() {
        Transaction transaction = transaction(user, food);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(food));
        when(transactionRepository.findByUserAndCategoryAndDateBetween(user, food,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(transaction)));

        var page = transactionService.getTransactions(user, LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31), 2L, null, PageRequest.of(0, 10));

        org.assertj.core.api.Assertions.assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void getTransactionsFiltersByTypeAndDateRange() {
        Transaction transaction = transaction(user, food);
        when(transactionRepository.findByUserAndCategoryTypeAndDateBetweenOrderByDateDesc(user, CategoryType.EXPENSE,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31))).thenReturn(List.of(transaction));

        var transactions = transactionService.getTransactions(user, LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31), null, CategoryType.EXPENSE);

        org.assertj.core.api.Assertions.assertThat(transactions).hasSize(1);
    }

    @Test
    void updateTransactionRejectsDateUpdates() {
        Transaction existing = transaction(user, food);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(existing));
        TransactionRequest request = new TransactionRequest();
        request.setDate(LocalDate.now());

        assertThatThrownBy(() -> transactionService.updateTransaction(5L, request, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("date cannot be updated");
    }

    @Test
    void updateTransactionCanChangeCategoryAndAmount() {
        Transaction existing = transaction(user, food);
        Category travel = Category.builder().id(3L).name("Travel").type(CategoryType.EXPENSE).user(user).build();
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(travel));
        when(transactionRepository.save(existing)).thenReturn(existing);
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("99.00"));
        request.setCategoryId(3L);

        var response = transactionService.updateTransaction(5L, request, user);

        org.assertj.core.api.Assertions.assertThat(response.getCategory()).isEqualTo("Travel");
        org.assertj.core.api.Assertions.assertThat(response.getAmount()).isEqualByComparingTo("99.00");
    }

    @Test
    void deleteTransactionDeletesOwnedTransaction() {
        Transaction existing = transaction(user, food);
        when(transactionRepository.findById(5L)).thenReturn(Optional.of(existing));

        transactionService.deleteTransaction(5L, user);

        verify(transactionRepository).delete(existing);
    }

    private Transaction transaction(User owner, Category category) {
        Transaction transaction = new Transaction();
        transaction.setId(5L);
        transaction.setUser(owner);
        transaction.setCategory(category);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setDate(LocalDate.now());
        transaction.setDescription("Lunch");
        return transaction;
    }
}
