package com.finance.manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Response for a yearly financial report aggregating income, expenses,
 * and net savings across all months of the given year.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YearlyReportResponse {

    /** The calendar year. */
    private int year;

    /** Category name → total income amount for the year. */
    private Map<String, BigDecimal> totalIncome;

    /** Category name → total expense amount for the year. */
    private Map<String, BigDecimal> totalExpenses;

    /** Net savings = total income − total expenses for the year. */
    private BigDecimal netSavings;
}
