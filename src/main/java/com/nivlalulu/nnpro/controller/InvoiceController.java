package com.nivlalulu.nnpro.controller;

import com.nivlalulu.nnpro.dto.InvoiceDto;
import com.nivlalulu.nnpro.dto.ProductDto;
import com.nivlalulu.nnpro.model.ApiResponse;
import com.nivlalulu.nnpro.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping(name = "/readAll")
    public ApiResponse<List<InvoiceDto>> getAllInvoices() {
        return new ApiResponse<>(HttpStatus.OK.value(), "All invoices", invoiceService.findAllInvoices());
    }

    @GetMapping(name = "/{id}")
    public ApiResponse<InvoiceDto> getInvoice(@PathVariable UUID id) {
        try {
            InvoiceDto invoiceDto = invoiceService.findInvoiceDtoById(id);
            return new ApiResponse<>(HttpStatus.OK.value(), String.format("Invoice id %s found", id), invoiceDto);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
        }
    }

    @PostMapping(name = "/saveInvoice")
    public ApiResponse<InvoiceDto> saveInvoice(@RequestBody InvoiceDto invoiceDto) {
        try {
            InvoiceDto invoice = invoiceService.createInvoice(invoiceDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly added invoice", invoice);
        } catch (Exception ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @PutMapping(name = "/updateInvoice")
    public ApiResponse<InvoiceDto> updateInvoice(@RequestBody InvoiceDto invoiceDto) {
        try {
            InvoiceDto updatedProduct = invoiceService.updateInvoice(invoiceDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly updated product", updatedProduct);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @PutMapping(name = "/addProducts/{id}")
    public ApiResponse<InvoiceDto> addProductToInvoice(@PathVariable UUID id, @RequestBody List<ProductDto> productsIds) {
        try {
            InvoiceDto updatedProduct = invoiceService.addProductToInvoice(id, productsIds);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly add products to invoice", updatedProduct);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @PutMapping(name = "/removeProducts/{id}")
    public ApiResponse<InvoiceDto> removeProductsFromInvoice(@PathVariable UUID id, @RequestBody List<ProductDto> productsIds) {
        try {
            InvoiceDto updatedProduct = invoiceService.removeProductFromInvoice(id, productsIds);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly removed products from invoice", updatedProduct);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @DeleteMapping(name = "/{id}")
    public ApiResponse<InvoiceDto> deleteInvoice(@PathVariable UUID id) {
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly deleted product", invoiceService.deleteInvoice(id));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }


}
