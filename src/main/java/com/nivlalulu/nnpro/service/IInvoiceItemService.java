package com.nivlalulu.nnpro.service;


import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IInvoiceItemService {
    /**
     * Finds all invoice items (paged) for the given user.
     */
    Page<InvoiceItemDto> findForInvoice(Invoice invoice, Pageable pageable);

    /**
     * Finds all invoice items for the given user.
     */
    List<InvoiceItemDto> findForInvoice(Invoice invoice);

    /**
     * Finds an invoice item by its ID for the given user.
     */
    InvoiceItemDto findInvoiceItemByIdForInvoice(UUID id, Invoice invoice);

    List<InvoiceItemDto> findByPrice(BigDecimal price);
}
