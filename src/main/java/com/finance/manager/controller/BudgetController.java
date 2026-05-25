package com.finance.manager.controller;

import com.finance.manager.dto.BudgetRequest;
import com.finance.manager.dto.BudgetResponse;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/{year}/{month}")
    public ResponseEntity<Map<String, List<BudgetResponse>>> getBudgets(@PathVariable int year,
                                                                        @PathVariable int month,
                                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(Map.of("budgets", budgetService.getMonthlyBudgets(userDetails.getUser(), year, month)));
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> upsertBudget(@Valid @RequestBody BudgetRequest request,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.upsertBudget(request, userDetails.getUser()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBudget(@PathVariable Long id,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        budgetService.deleteBudget(id, userDetails.getUser());
        return ResponseEntity.ok(Map.of("message", "Budget deleted successfully"));
    }
}
