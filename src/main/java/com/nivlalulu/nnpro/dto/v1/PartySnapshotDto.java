package com.nivlalulu.nnpro.dto.v1;

import com.nivlalulu.nnpro.common.controller.validation.OnCreate;
import com.nivlalulu.nnpro.common.controller.validation.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;


@Data
public class PartySnapshotDto {
    @Schema(description = "Organization or person getName", example = "John Doe")
    String name;
    @Schema(description = "Organization or person getAddress", example = "1234 Main St")
    String address;
    @Schema(description = "Organization or person getCountry", example = "Springfield")
    String country;
    @Schema(description = "Organization or person IC tax", example = "12345678")
    String icTax;
    @Schema(description = "Organization or person DIC tax", example = "12345678")
    String dicTax;
    @Schema(description = "Organization or person telephone", example = "+420 123 456 789")
    String telephone;
    @Schema(description = "Organization or person email", example = "nivlalulu@nivlalulu.test")
    String email;


    @AssertTrue(
            groups = {OnCreate.class},
            message = "Only one of IC or DIC tax must be provided"
    )
    private boolean isExactlyOnePresent() {
        boolean isOnlyOnePresent = (getIcTax() == null) != (getDicTax() == null);
        return isOnlyOnePresent;
    }

    @AssertTrue(
            groups = {OnUpdate.class},
            message = "Only one of IC or DIC tax can be provided"
    )
    private boolean isMaxOneTaxPresent() {
        boolean isNeitherPresent = getIcTax() == null && getDicTax() == null;
        if (isNeitherPresent) {
            return true;
        }
        boolean isOnlyOnePresent = (getIcTax() == null) != (getDicTax() == null);
        return isOnlyOnePresent;
    }

    public PartySnapshotDto(
            String name,
            String address,
            String country,
            String icTax,
            String dicTax,
            String telephone,
            String email) {
        this.name = name;
        this.address = address;
        this.country = country;
        this.icTax = icTax;
        this.dicTax = dicTax;
        this.telephone = telephone;
        this.email = email;
    }
}