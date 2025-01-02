package com.nivlalulu.nnpro.service;


import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    /**
     * Creates a new invoice item for the specified user.
     * Expects a fully populated InvoiceDto per the OnCreate validation rules.
     */
    InvoiceItemDto createInvoiceItem(InvoiceItemDto invoiceItemDto);

    /**
     * Partially updates an existing invoice item belonging to the given user.
     * Overwrites fields only if provided in the InvoiceItemDto (non-null).
     * If products is present (non-empty), does a full replace of items.
     * If customer / supplier is present, updates references.
     */
    InvoiceItemDto updateInvoiceItem(InvoiceItemDto invoiceItemDto);

    /**
     * Deletes the invoice item with the given ID.
     * Returns the deleted invoice item as a DTO for confirmation.
     */
    InvoiceItemDto deleteInvoiceItem(UUID id);
}
