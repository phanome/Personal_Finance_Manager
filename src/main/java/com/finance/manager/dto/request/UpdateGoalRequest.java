package com.finance.manager.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request payload for updating a savings goal's target amount or target date.
 */
@Data
public class UpdateGoalRequest {

    /** New target amount; must be positive if provided. */
    @DecimalMin(value = "0.01", message = "Target amount must be positive")
    private BigDecimal targetAmount;

    /** New target date; must be in the future if provided. */
    @Future(message = "Target date must be in the future")
    private LocalDate targetDate;
}
