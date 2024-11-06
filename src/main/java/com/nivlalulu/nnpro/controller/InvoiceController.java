package com.nivlalulu.nnpro.controller;

import com.nivlalulu.nnpro.dto.InvoiceDto;
import com.nivlalulu.nnpro.dto.ProductDto;
import com.nivlalulu.nnpro.model.ApiResponse;
import com.nivlalulu.nnpro.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    public ApiResponse<InvoiceDto> saveInvoice(@RequestBody InvoiceDto invoiceDto) {
        InvoiceDto invoice = invoiceService.createInvoice(invoiceDto);
        return invoice != null ?
                new ApiResponse<>(HttpStatus.OK.value(), "Successfuly added invoice", invoice) :
                new ApiResponse<>(HttpStatus.OK.value(), "Already exisiting product", null);
    }

    @PutMapping
    public ApiResponse<InvoiceDto> updateInvoice(@RequestBody InvoiceDto invoiceDto) {
        try {
            InvoiceDto updatedProduct = invoiceService.updateInvoice(invoiceDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly updated product", updatedProduct);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.OK.value(), "Product not found, can't be updated", null);
        }
    }

    @DeleteMapping
    public ApiResponse<InvoiceDto> deleteInvoice(@PathVariable UUID invoiceId) {
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly deleted product", invoiceService.deleteInvoice(invoiceId));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.OK.value(), "Product can't be deleted", null);
        }
    }


}
