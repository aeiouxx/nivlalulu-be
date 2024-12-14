package com.nivlalulu.nnpro.controller.v1;

import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.dto.ApiResponse;
import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.service.impl.InvoiceItemService;
import com.nivlalulu.nnpro.service.impl.MappingService;
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
@RequestMapping("/invoiceItem")
@Validated
public class InvoiceItemControllerV1 {

    @Autowired
    private InvoiceItemService invoiceItemService;

    @GetMapping("/all")
    public ApiResponse<List<InvoiceItemDto>> getInvoiceItems() {
        return new ApiResponse<>(HttpStatus.OK.value(), "All invoice items", invoiceItemService.findAllInvoiceItems());
    }

    @GetMapping("/{id}")
    public ApiResponse<InvoiceItemDto> getInvoiceItem(@PathVariable UUID id) {
        try {
            InvoiceItem invoiceItemDto = invoiceItemService.findProductById(id);
            return new ApiResponse<>(HttpStatus.OK.value(), String.format("Invoice item id %s found", id), MappingService.convertToDto(invoiceItemDto));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
        }
    }

    @PostMapping("/saveInvoiceItem")
    public ApiResponse<InvoiceItemDto> saveInvoiceItem(@Valid @RequestBody InvoiceItemDto invoiceItemDto) {
        try {
            InvoiceItemDto product = invoiceItemService.createInvoiceItem(invoiceItemDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly added product", product);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @PutMapping("/updateInvoiceItem")
    public ApiResponse<InvoiceItemDto> updateInvoiceItem(@Valid @RequestBody InvoiceItemDto invoiceItemDto) {
        try {
            InvoiceItemDto updatedProduct = invoiceItemService.updateInvoiceItem(invoiceItemDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly updated product", updatedProduct);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<InvoiceItemDto> deleteInvoiceItem(@PathVariable UUID id) {
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly deleted product", invoiceItemService.deleteInvoiceItem(id));
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
