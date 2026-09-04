package com.finance.manager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request payload for user registration.
 */
@Data
public class RegisterRequest {

    /** Must be a valid email address; used as the login username. */
    @NotBlank(message = "Username is required")
    @Email(message = "Username must be a valid email address")
    private String username;

    /** Plain-text password; will be encoded before storage. */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /** Full name of the user. */
    @NotBlank(message = "Full name is required")
    private String fullName;

    /** Contact phone number. */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,20}$", message = "Phone number is invalid")
    private String phoneNumber;
}
