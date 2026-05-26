package com.finance.manager.repository;

import com.finance.manager.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    @Query(value = """
            select *
            from budgets
            where user_id = :userId
              and budget_month = :budgetMonth
            """, nativeQuery = true)
    List<Budget> findByUserIdAndBudgetMonth(@Param("userId") Long userId,
                                            @Param("budgetMonth") String budgetMonth);

    @Query(value = """
            select *
            from budgets
            where user_id = :userId
              and category_id = :categoryId
              and budget_month = :budgetMonth
            """, nativeQuery = true)
    Optional<Budget> findByUserIdAndCategoryIdAndBudgetMonth(@Param("userId") Long userId,
                                                             @Param("categoryId") Long categoryId,
                                                             @Param("budgetMonth") String budgetMonth);
}
