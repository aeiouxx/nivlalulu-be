package com.nivlalulu.nnpro.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProductDto {

    @JsonIgnore
    private UUID id;

    private String name;

    private Integer quantity;

    private BigDecimal price; // raw price

    private BigDecimal taxPrice; // tax

    private BigDecimal totalPrice;
}
