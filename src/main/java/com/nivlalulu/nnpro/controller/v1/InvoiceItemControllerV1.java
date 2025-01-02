package com.nivlalulu.nnpro.controller.v1;

import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.dto.ApiResponse;
import com.nivlalulu.nnpro.dto.v1.UserDto;
import com.nivlalulu.nnpro.service.impl.InvoiceItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/product")
@RequiredArgsConstructor
@Validated
public class InvoiceItemControllerV1 {
    private final InvoiceItemService productService;

    @PostMapping("/saveProduct")
    @PreAuthorize("#invoiceItemDto.userId == authentication.principal.id")
    public ApiResponse<InvoiceItemDto> saveInvoiceItem(@Valid @RequestBody InvoiceItemDto invoiceItemDto, @AuthenticationPrincipal UserDto userDto) {
        try {
            InvoiceItemDto product = productService.createInvoiceItem(invoiceItemDto, userDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly added product", product);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @PutMapping("/updateProduct")
    @PreAuthorize("#invoiceItemDto.userId == authentication.principal.id")
    public ApiResponse<InvoiceItemDto> updateInvoiceItem(@Valid @RequestBody InvoiceItemDto invoiceItemDto, @AuthenticationPrincipal UserDto userDto) {
        try {
            InvoiceItemDto updatedProduct = productService.updateInvoiceItem(invoiceItemDto, userDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly updated product", updatedProduct);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<InvoiceItemDto> deleteInvoiceItem(@PathVariable UUID id, @AuthenticationPrincipal UserDto userDto) {
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly deleted product", productService.deleteInvoiceItem(id, userDto));
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
