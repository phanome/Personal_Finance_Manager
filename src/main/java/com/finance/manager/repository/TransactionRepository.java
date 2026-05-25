package com.finance.manager.repository;

import com.finance.manager.entity.Category;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    
    Optional<Transaction> findByIdAndUserAndDeletedFalse(Long id, User user);

   
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.user = :user AND t.category = :category AND t.deleted = false " +
           "ORDER BY t.date DESC, t.id DESC")
    List<Transaction> findByUserAndCategory(
            @Param("user") User user,
            @Param("category") Category category);

   
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.user = :user AND t.deleted = false " +
           "AND (coalesce(:startDate, t.date) <= t.date) " +
           "AND (coalesce(:endDate, t.date) >= t.date) " +
           "AND (coalesce(:categoryId, t.category.id) = t.category.id) " +
           "ORDER BY t.date DESC, t.id DESC")
    List<Transaction> findByFilters(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("categoryId") Long categoryId);

    
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.user = :user AND t.deleted = false " +
           "AND t.date >= :startDate AND t.date <= :endDate")
    List<Transaction> findByUserAndDateRange(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

   
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.user = :user AND t.deleted = false " +
           "AND t.category.type = :type " +
           "AND t.date >= :startDate")
    List<Transaction> findByUserAndTypeFromDate(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate);

    
    boolean existsByCategoryAndDeletedFalse(Category category);

    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user = :user AND t.deleted = false " +
           "AND t.category.type = :type " +
           "AND t.date >= :startDate")
    BigDecimal sumAmountByUserAndTypeFromDate(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate);
}
