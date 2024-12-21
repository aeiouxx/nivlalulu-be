package com.nivlalulu.nnpro.controller.v1;

import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.dto.ApiResponse;
import com.nivlalulu.nnpro.service.IInvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/invoice")
@RequiredArgsConstructor
@Validated
public class InvoiceControllerV1 {
    private final IInvoiceService invoiceService;

    @GetMapping("/readAll")
    public ApiResponse<List<InvoiceDto>> getAllInvoices() {
        return new ApiResponse<>(HttpStatus.OK.value(), "All invoices", invoiceService.findAllInvoices());
    }

    @GetMapping("/{id}")
    public ApiResponse<InvoiceDto> getInvoice(@PathVariable UUID id) {
        try {
            InvoiceDto invoiceDto = invoiceService.findInvoiceDtoById(id);
            return new ApiResponse<>(HttpStatus.OK.value(), String.format("Invoice id %s found", id), invoiceDto);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
        }
    }

    @PostMapping("/saveInvoice")
    @Operation(
            summary = "Save the invoice",
            description = "Changes the password for the specified user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The old and new password",
                    required = true,
                    content = @Content(schema = @Schema(implementation = InvoiceDto.class))
            )
    )
    public ApiResponse<InvoiceDto> saveInvoice(@Valid @RequestBody InvoiceDto invoiceDto) {
        try {
            InvoiceDto invoice = invoiceService.createInvoice(invoiceDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly added invoice", invoice);
        } catch (Exception ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @PutMapping("/updateInvoice")
    public ApiResponse<InvoiceDto> updateInvoice(@Valid @RequestBody InvoiceDto invoiceDto) {
        try {
            InvoiceDto updatedProduct = invoiceService.updateInvoice(invoiceDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly updated product", updatedProduct);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @PutMapping("/addProducts/{id}")
    public ApiResponse<InvoiceDto> addProductToInvoice(@PathVariable UUID id, @RequestBody List<@Valid ProductDto> productsIds) {
        try {
            InvoiceDto updatedProduct = invoiceService.addProductToInvoice(id, productsIds);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly add products to invoice", updatedProduct);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @PutMapping("/removeProducts/{id}")
    public ApiResponse<InvoiceDto> removeProductsFromInvoice(@PathVariable UUID id, @RequestBody List<@Valid ProductDto> productsIds) {
        try {
            InvoiceDto updatedProduct = invoiceService.removeProductFromInvoice(id, productsIds);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly removed products from invoice", updatedProduct);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<InvoiceDto> deleteInvoice(@PathVariable UUID id) {
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly deleted product", invoiceService.deleteInvoice(id));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

}
