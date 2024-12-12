package com.nivlalulu.nnpro.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class InvoiceDto {

    private UUID id;

    private String companyName;

    private String companyId;

    private String taxId;

    private Timestamp created;

    private Timestamp expiration;

    private List<UUID> productsId;

    private UUID user;


}
