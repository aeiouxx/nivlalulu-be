package com.nivlalulu.nnpro.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @Schema(description = "User username", example = "nivlalulu")
        @NotBlank String username,
        @Schema(description = "User password", example = "password")
        @NotBlank String password) { }
