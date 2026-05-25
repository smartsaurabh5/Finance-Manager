package com.finance.manager.controller;

import com.finance.manager.dto.PageResponse;
import com.finance.manager.dto.TransactionRequest;
import com.finance.manager.dto.TransactionResponse;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) CategoryType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        PageResponse<TransactionResponse> response = transactionService.getTransactions(userDetails.getUser(),
                startDate, endDate, categoryId, type, PageRequest.of(Math.max(page, 0), safeSize,
                        Sort.by(Sort.Direction.DESC, "date").and(Sort.by(Sort.Direction.DESC, "id"))));
        return ResponseEntity.ok(Map.of(
                "transactions", response.getContent(),
                "page", response.getPage(),
                "size", response.getSize(),
                "totalElements", response.getTotalElements(),
                "totalPages", response.getTotalPages(),
                "last", response.isLast()));
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<TransactionResponse>> getTransactionsPage(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) CategoryType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        return ResponseEntity.ok(transactionService.getTransactions(userDetails.getUser(), startDate, endDate,
                categoryId, type, PageRequest.of(Math.max(page, 0), safeSize,
                        Sort.by(Sort.Direction.DESC, "date").and(Sort.by(Sort.Direction.DESC, "id")))));
    }

    @GetMapping("/recent")
    public ResponseEntity<Map<String, List<TransactionResponse>>> getRecentTransactions(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(Map.of("transactions", transactionService.getRecentTransactions(userDetails.getUser())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable Long id,
                                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getTransaction(id, userDetails.getUser()));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.createTransaction(request, userDetails.getUser()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(@PathVariable Long id,
                                                                 @Valid @RequestBody TransactionRequest request,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, request, userDetails.getUser()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTransaction(@PathVariable Long id,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        transactionService.deleteTransaction(id, userDetails.getUser());
        return ResponseEntity.ok(Map.of("message", "Transaction deleted successfully"));
    }
}
