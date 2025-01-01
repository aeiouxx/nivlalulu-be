package com.nivlalulu.nnpro.dto.v1;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class UserDto {
    private Long id;

    private String username;

    private String email;

    private List<PartyDto> parties;
    private List<InvoiceDto> invoices;
    private List<InvoiceItemDto> invoiceItems;


}
