package com.nivlalulu.nnpro.controller.v1;

import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.service.IInvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/invoices")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Invoice management", description = "Operations related to managing invoices.")
public class InvoiceControllerV1 {
    private final IInvoiceService invoiceService;

    @Operation(
            summary = "List invoices (paginated)",
            description = "Returns a page of invoices along with pagination metadata."
    )
    @GetMapping
    @PageableAsQueryParam
    public Page<InvoiceDto> getInvoices(
            @AuthenticationPrincipal User user,
            final Pageable pageable) {
        log.info("User '{}' requested invoices with paging: {}", user.getUsername(), pageable);
        throw new NotImplementedException("Not implemented yet");
    }

    @Operation(
            summary = "Retrieve an invoice",
            description = "Returns the invoice with the specified customer facing identifier."
    )
    @GetMapping("/{id}")
    public InvoiceDto getInvoice(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        log.info("User '{}' requested invoice with id: {}", user.getUsername(), id);
        return invoiceService.findInvoiceByIdForUser(id, user);
    }

    @PostMapping
    @Operation(
            summary = "Create an invoice",
            description = "Creates a new invoice."
    )
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceDto createInvoice(
            @AuthenticationPrincipal User user,
            @Validated @RequestBody InvoiceDto invoiceDto) {
        log.info("User '{}' requested to create invoice: {}", user.getUsername(), invoiceDto);
        return invoiceService.createInvoice(invoiceDto);
    }

    @Operation(
            summary = "Update an invoice",
            description = "Updates an existing invoice."
    )
    @PutMapping("/{id}")
    public InvoiceDto updateInvoice(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Validated @RequestBody InvoiceDto invoiceDto) {
        log.info("User '{}' requested to update invoice with id: {}", user.getUsername(), id);
        return invoiceService.updateInvoice(invoiceDto);
    }

    @Operation(
            summary = "Delete an invoice",
            description = "Deletes an existing invoice."
    )
    @DeleteMapping("/{id}")
    public InvoiceDto deleteInvoice(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return invoiceService.deleteInvoice(id);
    }

}
