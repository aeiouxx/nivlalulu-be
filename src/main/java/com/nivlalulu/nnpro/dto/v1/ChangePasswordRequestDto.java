package com.nivlalulu.nnpro.dto.v1;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequestDto(
        @NotBlank
        String oldPassword,
        @NotBlank
        String newPassword
) {
}
