package com.nivlalulu.nnpro.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthenticationResponseDto(
        @Schema(description = "The access token")
        String accessToken,
        @Schema(description = "Username", example = "nivlalulu")
        String username)
{
}