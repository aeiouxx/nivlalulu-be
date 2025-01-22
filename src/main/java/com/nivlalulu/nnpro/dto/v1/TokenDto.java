package com.nivlalulu.nnpro.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

// ugly:
// literally all of this information is contained in the TOKEN, but we would need to switch to
// asymmetric encryption because we definitely dont want the FE to know the secret key
public record TokenDto(
        @JsonProperty("content")
        @Schema(description = "The encrypted token")
        String content,

        @JsonProperty("issued_at")
        @Schema(description = "The time the token was issued")
        Instant issuedAt,

        @JsonProperty("expires_at")
        @Schema(description = "The time the token expires")
        Instant expiresAt,

        // This field can technically lie, as the response can be received with a delay
        @JsonProperty("expires_in")
        @Schema(description = "Lifetime of the token in seconds " +
                "(can technically lie, as the response can be delayed)")
        long expiresIn
) {
}
