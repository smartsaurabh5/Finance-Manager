package com.finance.manager.repository;

import com.finance.manager.entity.Category;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByDateDesc(User user);
    Page<Transaction> findByUser(User user, Pageable pageable);
    List<Transaction> findByUserAndCategoryOrderByDateDesc(User user, Category category);
    Page<Transaction> findByUserAndCategory(User user, Category category, Pageable pageable);
    List<Transaction> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate);
    Page<Transaction> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<Transaction> findByUserAndCategoryAndDateBetweenOrderByDateDesc(User user, Category category, LocalDate startDate, LocalDate endDate);
    Page<Transaction> findByUserAndCategoryAndDateBetween(User user, Category category, LocalDate startDate, LocalDate endDate, Pageable pageable);
    boolean existsByCategory(Category category);
    List<Transaction> findTop5ByUserOrderByDateDesc(User user);

    @Query("select t from Transaction t where t.user = :user and t.category.type = :type order by t.date desc")
    List<Transaction> findByUserAndCategoryTypeOrderByDateDesc(User user, CategoryType type);

    @Query("select t from Transaction t where t.user = :user and t.category.type = :type")
    Page<Transaction> findByUserAndCategoryType(User user, CategoryType type, Pageable pageable);

    @Query("select t from Transaction t where t.user = :user and t.category.type = :type and t.date between :startDate and :endDate order by t.date desc")
    List<Transaction> findByUserAndCategoryTypeAndDateBetweenOrderByDateDesc(User user, CategoryType type, LocalDate startDate, LocalDate endDate);

    @Query("select t from Transaction t where t.user = :user and t.category.type = :type and t.date between :startDate and :endDate")
    Page<Transaction> findByUserAndCategoryTypeAndDateBetween(User user, CategoryType type, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
