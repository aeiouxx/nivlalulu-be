package com.nivlalulu.nnpro.dto.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
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

    @Schema(description = "Organization or person name", example = "Placeholder s.r.o.")
    private String name;

    @Schema(description = "Address", example = "123 Main St.")
    private String address;

    @Schema(description = "City", example = "New York")
    private String country;

    @JsonProperty("ic_tax")
    @Schema(description = "IC number", example = "123456")
    private String icTax;

    @JsonProperty("dic_tax")
    @Schema(description = "DIC number", example = "123456")
    private String dicTax;

    @Schema(description = "Telephone number", example = "+1234567890")
    private String telephone;

    @Schema(description = "Email address", example = "nivlalulu@nivlalulu.cz")
    private String email;

    @AssertTrue(message = "Only one of IC or DIC number can be provided")
    private boolean isIcOrDicProvided() {
        return (icTax == null) != (dicTax == null);
    }
}
