package com.finance.manager.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request payload for creating a new savings goal.
 */
@Data
public class GoalRequest {

    /** Descriptive name for the savings goal. */
    @NotBlank(message = "Goal name is required")
    private String goalName;

    /** The monetary target to reach; must be positive. */
    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "0.01", message = "Target amount must be positive")
    private BigDecimal targetAmount;

    /** The deadline for this goal; must be in the future. */
    @NotNull(message = "Target date is required")
    @Future(message = "Target date must be in the future")
    private LocalDate targetDate;

    /**
     * Optional start date; defaults to the current date if not provided.
     */
    private LocalDate startDate;
}
