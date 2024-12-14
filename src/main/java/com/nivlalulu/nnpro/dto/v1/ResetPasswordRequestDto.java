package com.nivlalulu.nnpro.dto.v1;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequestDto(
        @NotBlank
        String token,
        @NotBlank
        String newPassword
) {
}
