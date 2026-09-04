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

/**
 * Repository for {@link Transaction} entity operations.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Retrieves a non-deleted transaction by id belonging to a specific user.
     */
    Optional<Transaction> findByIdAndUserAndDeletedFalse(Long id, User user);

    /**
     * Returns non-deleted transactions for a user and category, ordered newest first.
     */
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.user = :user AND t.category = :category AND t.deleted = false " +
           "ORDER BY t.date DESC, t.id DESC")
    List<Transaction> findByUserAndCategory(
            @Param("user") User user,
            @Param("category") Category category);

    /**
     * Returns all non-deleted transactions for a user with optional filters,
     * ordered newest first.
     *
     * @param user       the owning user
     * @param startDate  optional lower bound on transaction date (inclusive)
     * @param endDate    optional upper bound on transaction date (inclusive)
     * @param categoryId optional category filter
     */
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

    /**
     * Returns all non-deleted transactions for a user within a date range
     * (used for monthly / yearly reports).
     */
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.user = :user AND t.deleted = false " +
           "AND t.date >= :startDate AND t.date <= :endDate")
    List<Transaction> findByUserAndDateRange(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Returns all non-deleted transactions of a specific type for a user
     * on or after the given start date (used for savings-goal progress).
     */
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.user = :user AND t.deleted = false " +
           "AND t.category.type = :type " +
           "AND t.date >= :startDate")
    List<Transaction> findByUserAndTypeFromDate(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate);

    /**
     * Checks whether any non-deleted transaction references the given category.
     *
     * @param category the category to check
     */
    boolean existsByCategoryAndDeletedFalse(Category category);

    /**
     * Sums amounts for a given user, transaction type, and date range.
     * Returns {@code null} when there are no matching rows (handle in service).
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user = :user AND t.deleted = false " +
           "AND t.category.type = :type " +
           "AND t.date >= :startDate")
    BigDecimal sumAmountByUserAndTypeFromDate(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate);
}
