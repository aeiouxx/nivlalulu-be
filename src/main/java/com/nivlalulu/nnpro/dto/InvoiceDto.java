package com.nivlalulu.nnpro.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nivlalulu.nnpro.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class InvoiceDto {

    @JsonIgnore
    private UUID id;

    private String companyName;

    private String companyId;

    private String taxId;

    private Timestamp created;

    private Timestamp expiration;

    private List<ProductDto> products;

    private PaymentMethod paymentMethod;

    private UserDto customer;

    private UserDto supplier;


}
