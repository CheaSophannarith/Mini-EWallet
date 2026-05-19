package com.fii.ewallet.admin.dto;

import jakarta.validation.constraints.*;

public record AgentUserRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,50}$",
                message = "Password must contain uppercase, lowercase, number, and special character"
        )
        String password,

        @NotNull(message = "Balance is required")
        @DecimalMin(value = "1000", message = "Minimum amount is 1000")
        @DecimalMax(value = "99999999", message = "Maximum amount is 99999999")
        Double balance

) {}
