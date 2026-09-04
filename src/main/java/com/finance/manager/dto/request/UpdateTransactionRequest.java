package com.finance.manager.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Request payload for updating an existing transaction.
 * The date field cannot be modified; all other fields are optional.
 */
@Data
public class UpdateTransactionRequest {

    /** New amount; must be positive if provided. */
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    /** New category name; optional. */
    private String category;

    /** New description; optional. */
    private String description;
}
