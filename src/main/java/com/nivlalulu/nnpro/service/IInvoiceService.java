package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.InvoiceSearchDto;
import com.nivlalulu.nnpro.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

public interface IInvoiceService {
    /**
     * Searches invoices with filters and pagination.
     */
    Page<InvoiceDto> search(User user, InvoiceSearchDto criteria, Pageable pageable, Sort sort);

    /**
     * Finds all invoices (paged) for the given user.
     */
    Page<InvoiceDto> findForUser(User user, Pageable pageable);

    /**
     * Finds all invoices for the given user.
     */
    List<InvoiceDto> findForUser(User user);

    /**
     * Finds an invoice by its ID for the given user.
     */
    InvoiceDto findInvoiceByIdForUser(UUID id, User user);

    /**
    * Creates a new invoice for the specified user.
    * Expects a fully populated InvoiceDto per the OnCreate validation rules.
    */
    InvoiceDto createInvoice(InvoiceDto invoiceDto, User user);

    /**
     * Partially updates an existing invoice belonging to the given user.
     * Overwrites fields only if provided in the InvoiceDto (non-null).
     * If products is present (non-empty), does a full replace of items.
     * If customer / supplier is present, updates references.
     */
    InvoiceDto updateInvoice(UUID id, InvoiceDto invoiceDto, User user);

    /**
    * Deletes the invoice with the given ID.
    * Returns the deleted invoice as a DTO for confirmation.
    */
    InvoiceDto deleteInvoice(UUID id);
}
