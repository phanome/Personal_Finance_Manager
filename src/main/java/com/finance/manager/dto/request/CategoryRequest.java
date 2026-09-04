package com.finance.manager.dto.request;

import com.finance.manager.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request payload for creating a custom category.
 */
@Data
public class CategoryRequest {

    /** Name of the new category; must be unique per user. */
    @NotBlank(message = "Category name is required")
    private String name;

    /** Whether this is an INCOME or EXPENSE category. */
    @NotNull(message = "Category type is required (INCOME or EXPENSE)")
    private TransactionType type;
}
