package com.finance.manager.repository;

import com.finance.manager.entity.Budget;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserAndMonth(User user, YearMonth month);
    Optional<Budget> findByUserAndCategoryAndMonth(User user, Category category, YearMonth month);
}
