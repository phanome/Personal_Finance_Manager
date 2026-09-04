package com.finance.manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Wrapper response containing a list of categories.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryListResponse {
    private List<CategoryResponse> categories;
}
