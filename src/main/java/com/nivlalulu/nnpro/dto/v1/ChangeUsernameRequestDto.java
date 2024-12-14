package com.nivlalulu.nnpro.dto.v1;

import jakarta.validation.constraints.NotBlank;

public record ChangeUsernameRequestDto(
        @NotBlank
        String newUsername
)
{
}
