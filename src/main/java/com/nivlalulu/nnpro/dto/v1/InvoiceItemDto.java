package com.nivlalulu.nnpro.dto.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String name;

    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "Tax cannot be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Tax must be greater than zero")
    private BigDecimal tax;

    @NotNull(message = "Total cannot be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Total must be greater than zero")
    private BigDecimal total;

    private Long userId;

    // Custom validation logic
    @AssertTrue(message = "Raw price plus tax must match total price")
    public boolean isTotalValid() {
        return price != null && tax != null && total != null
                && price.add(tax).compareTo(total) == 0;
    }
}
