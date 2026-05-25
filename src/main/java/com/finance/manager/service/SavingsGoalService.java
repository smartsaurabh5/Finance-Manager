package com.finance.manager.service;

import com.finance.manager.dto.SavingsGoalRequest;
import com.finance.manager.dto.SavingsGoalResponse;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.SavingsGoal;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.exception.ForbiddenException;
import com.finance.manager.exception.ResourceNotFoundException;
import com.finance.manager.repository.SavingsGoalRepository;
import com.finance.manager.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavingsGoalService {
    private final SavingsGoalRepository savingsGoalRepository;
    private final TransactionRepository transactionRepository;

    public SavingsGoalService(SavingsGoalRepository savingsGoalRepository, TransactionRepository transactionRepository) {
        this.savingsGoalRepository = savingsGoalRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<SavingsGoalResponse> getSavingsGoals(User user) {
        return savingsGoalRepository.findByUser(user).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public SavingsGoalResponse getSavingsGoal(Long id, User user) {
        return mapToResponse(getGoal(id, user));
    }

    @Transactional
    public SavingsGoalResponse createSavingsGoal(SavingsGoalRequest request, User user) {
        validateCreate(request);
        SavingsGoal goal = new SavingsGoal();
        goal.setGoalName(request.getGoalName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());
        goal.setStartDate(request.getStartDate() == null ? LocalDate.now() : request.getStartDate());
        goal.setUser(user);
        return mapToResponse(savingsGoalRepository.save(goal));
    }

    @Transactional
    public SavingsGoalResponse updateSavingsGoal(Long id, SavingsGoalRequest request, User user) {
        SavingsGoal goal = getGoal(id, user);
        if (request.getTargetAmount() != null) {
            goal.setTargetAmount(request.getTargetAmount());
        }
        if (request.getTargetDate() != null) {
            goal.setTargetDate(request.getTargetDate());
        }
        return mapToResponse(savingsGoalRepository.save(goal));
    }

    @Transactional
    public void deleteSavingsGoal(Long id, User user) {
        savingsGoalRepository.delete(getGoal(id, user));
    }

    private SavingsGoal getGoal(Long id, User user) {
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You don't have permission to access this savings goal");
        }
        return goal;
    }

    private void validateCreate(SavingsGoalRequest request) {
        if (request.getGoalName() == null || request.getGoalName().isBlank()) {
            throw new IllegalArgumentException("Goal name is required");
        }
        if (request.getTargetAmount() == null) {
            throw new IllegalArgumentException("Target amount is required");
        }
        if (request.getTargetDate() == null) {
            throw new IllegalArgumentException("Target date is required");
        }
        if (!request.getTargetDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Target date must be in the future");
        }
    }

    private SavingsGoalResponse mapToResponse(SavingsGoal goal) {
        List<Transaction> transactions = transactionRepository.findByUserAndDateBetweenOrderByDateDesc(
                goal.getUser(), goal.getStartDate(), LocalDate.now());
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            if (transaction.getCategory().getType() == CategoryType.INCOME) {
                income = income.add(transaction.getAmount());
            } else {
                expenses = expenses.add(transaction.getAmount());
            }
        }
        BigDecimal progress = income.subtract(expenses);
        if (progress.compareTo(BigDecimal.ZERO) < 0) {
            progress = BigDecimal.ZERO;
        }
        BigDecimal remaining = goal.getTargetAmount().subtract(progress);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }
        BigDecimal percentage = progress.divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);
        if (percentage.compareTo(new BigDecimal("100")) > 0) {
            percentage = new BigDecimal("100.00");
        }
        return new SavingsGoalResponse(goal.getId(), goal.getGoalName(), goal.getTargetAmount(), goal.getTargetDate(),
                goal.getStartDate(), progress, percentage, remaining);
    }
}
