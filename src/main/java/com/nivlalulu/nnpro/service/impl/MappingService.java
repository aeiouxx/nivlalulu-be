package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.dto.v1.PartyDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.model.Party;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MappingService {

    private static ModelMapper modelMapper;

    @Autowired
    public MappingService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public static InvoiceItemDto convertToDto(InvoiceItem invoiceItem) {
        return modelMapper.map(invoiceItem, InvoiceItemDto.class);
    }

    public static PartyDto convertToDto(Party party) {
        return modelMapper.map(party, PartyDto.class);
    }

    public static InvoiceDto convertToDto(Invoice invoice) {
        return modelMapper.map(invoice, InvoiceDto.class);
    }

    public static InvoiceItem convertToEntity(InvoiceItemDto invoiceItemDto) {
        return modelMapper.map(invoiceItemDto, InvoiceItem.class);
    }

    public static Invoice convertToEntity(InvoiceDto invoiceDto) {
        return modelMapper.map(invoiceDto, Invoice.class);
    }

    public static Party convertToEntity(PartyDto partyDto) {
        return modelMapper.map(partyDto, Party.class);
    }


}
