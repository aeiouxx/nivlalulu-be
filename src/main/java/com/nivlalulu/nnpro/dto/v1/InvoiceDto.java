package com.nivlalulu.nnpro.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivlalulu.nnpro.common.controller.validation.OnCreate;
import com.nivlalulu.nnpro.common.controller.validation.OnUpdate;
import com.nivlalulu.nnpro.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class InvoiceDto {

    @NotNull(groups = OnUpdate.class, message = "ID cannot be null")
    @Null(groups = OnCreate.class, message = "ID must be null")
    @Schema(
            description =
                    "Unique identifier of the invoice, only used in updates, " +
                    "must be null when creating a new invoice",
            example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @JsonProperty("created_at")
    @NotNull(groups = OnCreate.class, message = "Issue date cannot be null")
    @Schema(description = "Date when the invoice was issued", example = "2021-01-01T00:00:00Z")
    private Instant createdAt;

    @JsonProperty("expires_at")
    @NotNull(groups = OnCreate.class, message = "Due date cannot be null")
    @Schema(description = "Date when the invoice is due", example = "2021-01-01T00:00:00Z")
    private Instant expiresAt;

    @NotNull(groups = OnCreate.class, message = "Products cannot be null")
    @NotEmpty(groups = OnCreate.class, message = "Products cannot be empty")
    private List<@Valid InvoiceItemDto> items;

    @NotNull(groups = OnCreate.class, message = "Payment method cannot be null")
    @Schema(description = "Payment method", example = "P")
    private PaymentMethod paymentMethod;

    @JsonProperty("variable_symbol")
    @NotBlank(groups = OnCreate.class, message = "Variable symbol cannot be blank")
    @Schema(description = "Variable symbol", example = "123456")
    private String variableSymbol;

    @NotNull(groups = OnCreate.class, message = "Customer cannot be null")
    @Valid
    private PartySnapshotDto customer;

    @NotNull(groups = OnCreate.class, message = "Supplier cannot be null")
    @Valid
    private PartySnapshotDto supplier;

    @JsonProperty("raw_value")
    @NotNull(groups = OnCreate.class, message = "Raw value cannot be null")
    @DecimalMin(value = "0.00", inclusive = true, message = "Raw value must be non-negative")
    @Schema(description = "Raw value of the invoice", example = "100.00")
    private BigDecimal rawValue;

    @JsonProperty("tax_value")
    @NotNull(groups = OnCreate.class, message = "Tax value cannot be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Tax value must be non-negative")
    @Schema(description = "Tax value of the invoice", example = "21.00")
    private BigDecimal taxValue;

    @JsonProperty("total_value")
    @NotNull(groups = OnCreate.class, message = "Total value cannot be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Total value must be non-negative")
    @Schema(description = "Total value of the invoice", example = "121.00")
    private BigDecimal totalValue;

    @JsonProperty("contact")
    @Schema(description = "Arbitrary contact information", example = "John Doe")
    private String contact;


    @AssertTrue(
            groups = {OnCreate.class},
            message = "Raw value plus tax value must match total value")
    public boolean onCreateTotalValueValid() {
        return rawValue != null && taxValue != null && totalValue != null
                && rawValue.add(taxValue).compareTo(totalValue) == 0;
    }

    @AssertTrue(
            groups = {OnUpdate.class},
            message = "Raw value plus tax value must match total value")
    public boolean onUpdateTotalValueValidIfIncluded() {
        if (rawValue == null || taxValue == null || totalValue == null) {
            return true;
        }
        return rawValue.add(taxValue).compareTo(totalValue) == 0;
    }

}
