package com.nivlalulu.nnpro.dto.v1;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequestDto(
        @NotNull
        String refreshToken
)
{
}

