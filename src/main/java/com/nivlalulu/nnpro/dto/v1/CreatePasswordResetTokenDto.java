package com.nivlalulu.nnpro.dto.v1;

import jakarta.validation.constraints.NotBlank;

public record CreatePasswordResetTokenDto(
        @NotBlank
        String username
) {
}
