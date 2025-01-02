package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.dto.v1.UserDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.InvoiceItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IInvoiceService {
    InvoiceDto createInvoice(InvoiceDto invoiceDto, UserDto userDto);

    InvoiceDto updateInvoice(InvoiceDto invoiceUpdated, UserDto userDto);

    InvoiceDto deleteInvoice(UUID id, UserDto userDto);

    InvoiceDto addInvoiceItemToInvoice(UUID invoiceId, List<InvoiceItemDto> productsIds, UserDto userDto);

    InvoiceDto removeInvoiceItemFromInvoice(UUID invoiceId, List<InvoiceItemDto> productsIds, UserDto userDto);

    InvoiceDto findInvoiceDtoById(UUID id, UserDto userDto);

    Optional<Invoice> findInvoiceById(UUID id, UserDto userDto);

    List<InvoiceDto> findAllInvoices();

    List<Invoice> findAllContainsInvoiceItem(InvoiceItem product);
}
