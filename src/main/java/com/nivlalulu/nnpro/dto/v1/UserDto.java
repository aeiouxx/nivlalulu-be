package com.nivlalulu.nnpro.dto.v1;

import java.util.List;

public class UserDto {
    private Long id;

    private String username;

    private String email;

    private List<PartyDto> parties;
    private List<InvoiceDto> invoices;
    private List<InvoiceItemDto> invoiceItems;


}
