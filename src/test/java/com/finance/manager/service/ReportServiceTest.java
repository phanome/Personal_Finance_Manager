package com.finance.manager.service;

import com.finance.manager.dto.response.MonthlyReportResponse;
import com.finance.manager.dto.response.YearlyReportResponse;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.enums.TransactionType;
import com.finance.manager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ReportService}.
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock private TransactionRepository transactionRepository;

    @InjectMocks
    private ReportService reportService;

    private User user;
    private Category salaryCategory;
    private Category foodCategory;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("test@example.com").build();

        salaryCategory = Category.builder()
                .id(1L).name("Salary").type(TransactionType.INCOME).custom(false).build();

        foodCategory = Category.builder()
                .id(2L).name("Food").type(TransactionType.EXPENSE).custom(false).build();
    }

    private Transaction makeTransaction(Category category, BigDecimal amount, LocalDate date) {
        return Transaction.builder()
                .amount(amount).date(date).category(category)
                .user(user).deleted(false).build();
    }

    // -----------------------------------------------------------------------
    // Monthly report
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getMonthlyReport - correct income, expense, and net savings")
    void getMonthlyReport_correctAggregation() {
        List<Transaction> txns = List.of(
                makeTransaction(salaryCategory, new BigDecimal("3000.00"), LocalDate.of(2024, 1, 15)),
                makeTransaction(salaryCategory, new BigDecimal("500.00"),  LocalDate.of(2024, 1, 20)),
                makeTransaction(foodCategory,   new BigDecimal("400.00"),  LocalDate.of(2024, 1, 10)),
                makeTransaction(foodCategory,   new BigDecimal("200.00"),  LocalDate.of(2024, 1, 25))
        );

        when(transactionRepository.findByUserAndDateRange(
                eq(user),
                eq(LocalDate.of(2024, 1, 1)),
                eq(LocalDate.of(2024, 1, 31))))
            .thenReturn(txns);

        MonthlyReportResponse report = reportService.getMonthlyReport(2024, 1, user);

        assertThat(report.getMonth()).isEqualTo(1);
        assertThat(report.getYear()).isEqualTo(2024);
        assertThat(report.getTotalIncome().get("Salary"))
                .isEqualByComparingTo("3500.00");
        assertThat(report.getTotalExpenses().get("Food"))
                .isEqualByComparingTo("600.00");
        assertThat(report.getNetSavings()).isEqualByComparingTo("2900.00");
    }

    @Test
    @DisplayName("getMonthlyReport - empty month returns zeroed values")
    void getMonthlyReport_empty() {
        when(transactionRepository.findByUserAndDateRange(any(), any(), any()))
                .thenReturn(List.of());

        MonthlyReportResponse report = reportService.getMonthlyReport(2024, 2, user);

        assertThat(report.getTotalIncome()).isEmpty();
        assertThat(report.getTotalExpenses()).isEmpty();
        assertThat(report.getNetSavings()).isEqualByComparingTo("0.00");
    }

    // -----------------------------------------------------------------------
    // Yearly report
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getYearlyReport - aggregates across full year")
    void getYearlyReport_correctAggregation() {
        List<Transaction> txns = List.of(
                makeTransaction(salaryCategory, new BigDecimal("36000.00"), LocalDate.of(2024, 6, 1)),
                makeTransaction(foodCategory,   new BigDecimal("4800.00"),  LocalDate.of(2024, 6, 5))
        );

        when(transactionRepository.findByUserAndDateRange(
                eq(user),
                eq(LocalDate.of(2024, 1, 1)),
                eq(LocalDate.of(2024, 12, 31))))
            .thenReturn(txns);

        YearlyReportResponse report = reportService.getYearlyReport(2024, user);

        assertThat(report.getYear()).isEqualTo(2024);
        assertThat(report.getTotalIncome().get("Salary")).isEqualByComparingTo("36000.00");
        assertThat(report.getTotalExpenses().get("Food")).isEqualByComparingTo("4800.00");
        assertThat(report.getNetSavings()).isEqualByComparingTo("31200.00");
    }

    @Test
    @DisplayName("getYearlyReport - negative net savings when expenses > income")
    void getYearlyReport_negativeSavings() {
        List<Transaction> txns = List.of(
                makeTransaction(salaryCategory, new BigDecimal("1000.00"), LocalDate.of(2024, 3, 1)),
                makeTransaction(foodCategory,   new BigDecimal("2000.00"), LocalDate.of(2024, 3, 5))
        );

        when(transactionRepository.findByUserAndDateRange(any(), any(), any())).thenReturn(txns);

        YearlyReportResponse report = reportService.getYearlyReport(2024, user);

        assertThat(report.getNetSavings()).isEqualByComparingTo("-1000.00");
    }
}
