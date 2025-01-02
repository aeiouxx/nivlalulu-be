package com.nivlalulu.nnpro.dto.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItemDto {

    @JsonIgnore
    private UUID id;

    @NotNull(message = "Product name cannot be null")
    @NotEmpty(message = "Product name cannot be empty")
    @Schema(description = "Name of the product", example = "Product 1")
    private String name;

    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be greater than zero")
    @Schema(description = "Quantity of the product", example = "1")
    private Integer quantity;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.00", inclusive = true, message = "Price must be non-negative.")
    @Schema(description = "Unit price of the product", example = "100.00")
    private BigDecimal unitPrice;

    @NotNull(message = "Tax cannot be null")
    @DecimalMin(value = "0.00", inclusive = true, message = "Tax must be non-negative.")
    @Schema(description = "Tax price of the product", example = "21.00")
    private BigDecimal taxPrice;

    @NotNull(message = "Total cannot be null")
    @DecimalMin(value = "0.00", inclusive = true, message = "Total must be non-negative.")
    @Schema(description = "Total price of the product", example = "121.00")
    private BigDecimal totalPrice;

    // Custom validation logic
    @AssertTrue(message = "Raw price plus tax must match total price")
    public boolean isTotalValid() {
        return unitPrice != null && taxPrice != null && totalPrice != null
                && unitPrice.add(taxPrice).compareTo(totalPrice) == 0;
    }
}
