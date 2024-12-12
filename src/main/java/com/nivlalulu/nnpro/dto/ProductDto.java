package com.nivlalulu.nnpro.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProductDto {

    private UUID id;

    private String name;

    private Integer quantity;

    private BigDecimal price;

    private UUID invoiceId;

}
