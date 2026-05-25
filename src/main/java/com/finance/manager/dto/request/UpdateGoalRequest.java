package com.finance.manager.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
public class UpdateGoalRequest {

    
    @DecimalMin(value = "0.01", message = "Target amount must be positive")
    private BigDecimal targetAmount;

   
    @Future(message = "Target date must be in the future")
    private LocalDate targetDate;
}
