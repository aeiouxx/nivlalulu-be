package com.nivlalulu.nnpro.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record AuthenticationResponseDto(
        @JsonProperty("access_token")
        @Schema(description = "The access token information")
        TokenDto accessToken,
        @Schema(description = "Username", example = "nivlalulu")
        String username)
{
}