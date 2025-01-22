package com.nivlalulu.nnpro.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeUsernameResponseDto(
        @Schema(description = "The new username")
        @JsonProperty("new_username")
        String newUsername,
        @Schema(description = "The new access token information")
        TokenDto tokenDto
) {
}
