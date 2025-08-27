package com.gca.dto;

import com.gca.security.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 1, max = 100, message = "Username must be 1-100 characters")
    private String username;

    @NotBlank(message = "Password is required")
    private String oldPassword;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    @Pattern(
            regexp = ValidationPatterns.PASSWORD_PATTERN,
            message = "Password must be at least 8 characters long, contain upper and lower case letters, a digit, and a special character"
    )
    private String newPassword;
}
