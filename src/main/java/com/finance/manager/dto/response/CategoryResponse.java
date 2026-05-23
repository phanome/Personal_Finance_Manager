package com.finance.manager.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response representation of a category (default or custom).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

    private String name;
    private String type;

    /**
     * {@code true} when this is a user-defined category.
     * Serialised as {@code "isCustom"} in the JSON output.
     */
    @JsonProperty("isCustom")
    private boolean custom;
}
