package com.finance.manager.controller;

import com.finance.manager.dto.SavingsGoalRequest;
import com.finance.manager.dto.SavingsGoalResponse;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.SavingsGoalService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/goals", "/api/savings-goals"})
public class SavingsGoalController {
    private final SavingsGoalService savingsGoalService;

    public SavingsGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    @GetMapping
    public ResponseEntity<Map<String, List<SavingsGoalResponse>>> getSavingsGoals(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(Map.of("goals", savingsGoalService.getSavingsGoals(userDetails.getUser())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> getSavingsGoal(@PathVariable Long id,
                                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(savingsGoalService.getSavingsGoal(id, userDetails.getUser()));
    }

    @PostMapping
    public ResponseEntity<SavingsGoalResponse> createSavingsGoal(@Valid @RequestBody SavingsGoalRequest request,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savingsGoalService.createSavingsGoal(request, userDetails.getUser()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> updateSavingsGoal(@PathVariable Long id,
                                                                 @Valid @RequestBody SavingsGoalRequest request,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(savingsGoalService.updateSavingsGoal(id, request, userDetails.getUser()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSavingsGoal(@PathVariable Long id,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        savingsGoalService.deleteSavingsGoal(id, userDetails.getUser());
        return ResponseEntity.ok(Map.of("message", "Goal deleted successfully"));
    }
}
