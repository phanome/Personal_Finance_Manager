package com.finance.manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Wrapper response containing a list of transactions.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionListResponse {
    private List<TransactionResponse> transactions;
}
