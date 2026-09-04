package com.finance.manager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request payload for user login.
 */
@Data
public class LoginRequest {

    /** Email used as the login credential. */
    @NotBlank(message = "Username is required")
    @Email(message = "Username must be a valid email address")
    private String username;

    /** Plain-text password to authenticate with. */
    @NotBlank(message = "Password is required")
    private String password;
}
