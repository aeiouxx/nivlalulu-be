package com.nivlalulu.nnpro.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivlalulu.nnpro.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class InvoiceDto {

    @JsonIgnore
    private UUID id;

    @JsonProperty("number")
    @NotNull(message = "Invoice number cannot be null")
    private BigDecimal invoiceNo;

    @JsonProperty("issue_date")
    @NotNull(message = "Issue date cannot be null")
    private Timestamp issueDate;

    @JsonProperty("due_date")
    @NotNull(message = "Due date cannot be null")
    private Timestamp dueDate;

    @NotNull(message = "Products cannot be null")
    @NotEmpty(message = "Products cannot be empty")
    private List<@Valid InvoiceItemDto> products;

    @NotNull(message = "Payment method cannot be null")
    private PaymentMethod paymentMethod;

    private String variableSymbol;

    @NotNull(message = "Customer cannot be null")
    @Valid
    private PartyDto customer;

    @NotNull(message = "Supplier cannot be null")
    @Valid
    private PartyDto supplier;

    @JsonProperty("raw_value")
    @NotNull(message = "Raw value cannot be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Raw value must be greater than zero")
    private BigDecimal rawValue;

    @JsonProperty("tax_value")
    @NotNull(message = "Tax value cannot be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Tax value must be greater than zero")
    private BigDecimal taxValue;

    @JsonProperty("total_value")
    @NotNull(message = "Total value cannot be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Total value must be greater than zero")
    private BigDecimal totalValue;

    // Custom validation logic
    @AssertTrue(message = "Raw value plus tax value must match total value")
    public boolean isTotalValueValid() {
        return rawValue != null && taxValue != null && totalValue != null
                && rawValue.add(taxValue).compareTo(totalValue) == 0;
    }

}
