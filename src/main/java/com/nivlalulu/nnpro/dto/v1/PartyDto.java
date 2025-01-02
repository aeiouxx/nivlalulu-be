package com.nivlalulu.nnpro.dto.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PartyDto {
    @JsonIgnore
    private UUID id;

    @Schema(description = "Organization name", example = "Company 1")
    private String organizationName;

    @Schema(description = "Person name", example = "John Doe")
    private String personName;

    @Schema(description = "Address", example = "123 Main St.")
    private String address;

    @Schema(description = "City", example = "New York")
    private String country;

    @Schema(description = "Id of the company", example = "123456")
    private String companyId;

    @Schema(description = "Tax ID", example = "123456")
    private String taxId;

    @Schema(description = "Telephone number", example = "+1234567890")
    private String telephone;

    @Schema(description = "Email address", example = "")
    private String email;
}
