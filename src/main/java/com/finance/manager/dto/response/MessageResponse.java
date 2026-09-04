package com.finance.manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic response containing only a message string.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String message;
}
