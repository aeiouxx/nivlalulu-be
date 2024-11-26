package com.nivlalulu.nnpro.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivlalulu.nnpro.enums.PaymentMethod;
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
    private BigDecimal invoiceNo;

    @JsonProperty("issue_date")
    private Timestamp issueDate;

    @JsonProperty("due_date")
    private Timestamp dueDate;

    private List<ProductDto> products;

    private PaymentMethod paymentMethod;

    private UserDto customer;

    private UserDto supplier;

    @JsonProperty("raw_value")
    private BigDecimal rawValue;

    @JsonProperty("tax_value")
    private BigDecimal taxValue;

    @JsonProperty("total_value")
    private BigDecimal totalValue;

}
