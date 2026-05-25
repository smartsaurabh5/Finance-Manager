package com.finance.manager.repository;

import com.finance.manager.entity.Category;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByDateDesc(User user);
    List<Transaction> findByUserAndCategoryOrderByDateDesc(User user, Category category);
    List<Transaction> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate);
    List<Transaction> findByUserAndCategoryAndDateBetweenOrderByDateDesc(User user, Category category, LocalDate startDate, LocalDate endDate);
    boolean existsByCategory(Category category);

    @Query("select t from Transaction t where t.user = :user and t.category.type = :type order by t.date desc")
    List<Transaction> findByUserAndCategoryTypeOrderByDateDesc(User user, CategoryType type);

    @Query("select t from Transaction t where t.user = :user and t.category.type = :type and t.date between :startDate and :endDate order by t.date desc")
    List<Transaction> findByUserAndCategoryTypeAndDateBetweenOrderByDateDesc(User user, CategoryType type, LocalDate startDate, LocalDate endDate);
}
