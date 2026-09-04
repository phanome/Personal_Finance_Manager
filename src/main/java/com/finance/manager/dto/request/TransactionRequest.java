package com.finance.manager.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request payload for creating a new transaction.
 */
@Data
public class TransactionRequest {

    /** Monetary amount; must be positive. */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    /** Transaction date in YYYY-MM-DD format; must not be in the future. */
    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;

    /** Name of the category this transaction belongs to. */
    @NotBlank(message = "Category is required")
    private String category;

    /** Optional description of the transaction. */
    private String description;
}
