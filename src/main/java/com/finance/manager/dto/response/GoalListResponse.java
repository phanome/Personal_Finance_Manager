package com.finance.manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Wrapper response containing a list of savings goals.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalListResponse {
    private List<GoalResponse> goals;
}
