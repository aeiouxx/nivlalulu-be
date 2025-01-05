package com.nivlalulu.nnpro.dto.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nivlalulu.nnpro.common.controller.validation.OnCreate;
import com.nivlalulu.nnpro.common.controller.validation.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;

/**
 * A snapshot of a party (organization or person) at a time the invoice was created.
 */
public record PartySnapshotDto (
        @Schema(description = "Organization or person name", example = "John Doe")
        String name,
        @Schema(description = "Organization or person address", example = "1234 Main St")
        String address,
        @Schema(description = "Organization or person country", example = "Springfield")
        String country,
        @Schema(description = "Organization or person IC tax", example = "12345678")
        String icTax,
        @Schema(description = "Organization or person DIC tax", example = "12345678")
        String dicTax,
        @Schema(description = "Organization or person telephone", example = "+420 123 456 789")
        String telephone,
        @Schema(description = "Organization or person email", example = "nivlalulu@nivlalulu.test")
        String email
)
{
    @AssertTrue(
            groups = {OnCreate.class},
            message = "Either IC or DIC tax must be present"
    )
    private boolean isExactlyOnePresent() {
        boolean isOnlyOnePresent = (icTax() == null) != (dicTax() == null);
        return isOnlyOnePresent;
    }

    @AssertTrue(
            groups = {OnUpdate.class},
            message = "Only one of IC or DIC tax can be provided"
    )
    private boolean isMaxOneTaxPresent() {
        boolean isNeitherPresent = icTax() == null && dicTax() == null;
        if (isNeitherPresent) {
            return true;
        }
        boolean isOnlyOnePresent = (icTax() == null) != (dicTax() == null);
        return isOnlyOnePresent;
    }
}