package com.nivlalulu.nnpro.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record RefreshTokenResponseDto(
        @Schema(description = "The access token")
        String accessToken) {}
