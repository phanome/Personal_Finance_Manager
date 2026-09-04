package com.finance.manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Response for a monthly financial report showing income, expenses,
 * and net savings broken down by category.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyReportResponse {

    /** The calendar month (1–12). */
    private int month;

    /** The calendar year. */
    private int year;

    /** Category name → total income amount. */
    private Map<String, BigDecimal> totalIncome;

    /** Category name → total expense amount. */
    private Map<String, BigDecimal> totalExpenses;

    /** Net savings = total income − total expenses. */
    private BigDecimal netSavings;
}
