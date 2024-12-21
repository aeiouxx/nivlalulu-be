package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.InvoiceItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IInvoiceService {
    InvoiceDto createInvoice(InvoiceDto invoiceDto);

    InvoiceDto updateInvoice(InvoiceDto invoiceUpdated);

    InvoiceDto deleteInvoice(UUID id);

    InvoiceDto addInvoiceItemToInvoice(UUID invoiceId, List<InvoiceItemDto> productsIds);

    InvoiceDto removeInvoiceItemFromInvoice(UUID invoiceId, List<InvoiceItemDto> productsIds);

    InvoiceDto findInvoiceDtoById(UUID id);

    Optional<Invoice> findInvoiceById(UUID id);

    List<InvoiceDto> findAllInvoices();

    List<Invoice> findAllContainsInvoiceItem(InvoiceItem product);
}
