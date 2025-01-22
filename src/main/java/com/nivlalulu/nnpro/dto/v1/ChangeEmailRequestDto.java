package com.nivlalulu.nnpro.dto.v1;

import jakarta.validation.constraints.Email;

public record ChangeEmailRequestDto(
        @Email
        String newEmail
) {
}
