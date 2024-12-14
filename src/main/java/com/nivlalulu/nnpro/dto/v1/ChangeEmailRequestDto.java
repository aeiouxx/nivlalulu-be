package com.nivlalulu.nnpro.dto.v1;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailRequestDto(
        @Email
        String newEmail
) {
}
