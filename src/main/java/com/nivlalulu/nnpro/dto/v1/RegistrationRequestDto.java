package com.nivlalulu.nnpro.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record RegistrationRequestDto(
        @Schema(description = "Username", example = "admin")
        @NotBlank(message = "Username is required.")
        String username,
        @Schema(description = "Email", example = "admin@network.io")
        @NotBlank(message = "Email is required.")
        @Email(message = "Email must be valid")
        String email,
        @Schema(description = "Password", example = "Password.12345678")
        @NotBlank(message = "Password is required.")
        @Length(min = 8, message = "Password must be at least 8 characters long.")
        String password
) {
}
