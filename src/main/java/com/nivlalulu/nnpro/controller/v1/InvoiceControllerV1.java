package com.nivlalulu.nnpro.controller.v1;

import com.nivlalulu.nnpro.common.controller.validation.OnCreate;
import com.nivlalulu.nnpro.common.controller.validation.OnUpdate;
import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.InvoiceSearchDto;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.service.IInvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Operation(summary = "Search invoices with filters, sorting, and pagination")
    @GetMapping("/search")
    @PageableAsQueryParam
    public Page<InvoiceDto> searchInvoices(
            @AuthenticationPrincipal User user,
            @ModelAttribute InvoiceSearchDto searchDto,
            @RequestParam(required = false) Pageable pageable,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        log.info("User '{}' requested to search invoices", user.getUsername());
        Sort sort = null;
        if (sortBy != null && sortOrder != null) {
            sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        }
        return invoiceService.search(user, searchDto, pageable, sort);
    }

    @Operation(
            summary = "List invoices (optionally paginated)",
            description = """
            Returns a (paginated) list of invoices.
            Supports sorting by the following fields:
            - `createdAt`
            - `expiresAt`
            - `contact`.
            
            Sort format: `fieldName,asc|desc`.
            """
    )
    @GetMapping
    @PageableAsQueryParam
    public Page<InvoiceDto> getInvoices(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false)
            Pageable pageable) {
        if (pageable == null) {
            log.debug("No paging information provided. Returning all invoices.");
            pageable = Pageable.unpaged();
        }
        log.info("User '{}' requested invoices with paging: {}", user.getUsername(), pageable);
        return invoiceService.findForUser(user, pageable);
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
            description = "Creates a new invoice.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Invoice to create",
                required = true,
                content = @Content(schema = @Schema(implementation = InvoiceDto.class)))
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Invoice created",
                    content = @Content(schema = @Schema(implementation = InvoiceDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceDto createInvoice(
            @AuthenticationPrincipal User user,
            @Validated(OnCreate.class) @RequestBody InvoiceDto invoiceDto) {
        log.info("User '{}' requested to create invoice: {}", user.getUsername(), invoiceDto);
        return invoiceService.createInvoice(invoiceDto, user);
    }

    @Operation(
            summary = "Update an invoice",
            description = "Updates an existing invoice."
    )
    @PutMapping("/{id}")
    public InvoiceDto updateInvoice(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Validated(OnUpdate.class) @RequestBody InvoiceDto invoiceDto) {
        log.info("User '{}' requested to update invoice with id: {}", user.getUsername(), id);
        return invoiceService.updateInvoice(id, invoiceDto, user);
    }

    @Operation(
            summary = "Delete an invoice",
            description = "Deletes an existing invoice."
    )
    @DeleteMapping("/{id}")
    public InvoiceDto deleteInvoice(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        log.info("User '{}' requested to delete invoice with id: {}", user.getUsername(), id);
        return invoiceService.deleteInvoice(id);
    }
}
