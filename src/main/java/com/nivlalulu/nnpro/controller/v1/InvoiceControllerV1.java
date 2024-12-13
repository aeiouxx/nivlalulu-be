package com.nivlalulu.nnpro.controller.v1;

import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.dto.ApiResponse;
import com.nivlalulu.nnpro.service.impl.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/invoice")
@Validated
public class InvoiceControllerV1 {

    @Autowired
    private InvoiceService invoiceService;

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
    public ApiResponse<InvoiceDto> addProductToInvoice(@PathVariable UUID id, @RequestBody List<@Valid InvoiceItemDto> productsIds) {
        try {
            InvoiceDto updatedProduct = invoiceService.addProductToInvoice(id, productsIds);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly add products to invoice", updatedProduct);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @PutMapping("/removeProducts/{id}")
    public ApiResponse<InvoiceDto> removeProductsFromInvoice(@PathVariable UUID id, @RequestBody List<@Valid InvoiceItemDto> productsIds) {
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, null);
    }


}
