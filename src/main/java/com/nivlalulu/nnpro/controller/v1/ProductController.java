package com.nivlalulu.nnpro.controller.v1;

import com.nivlalulu.nnpro.dto.v1.ProductDto;
import com.nivlalulu.nnpro.dto.ApiResponse;
import com.nivlalulu.nnpro.service.impl.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/product")
@RequiredArgsConstructor
@Validated
public class ProductController {
    private final ProductService productService;

    @PostMapping("/saveProduct")
    public ApiResponse<ProductDto> saveProduct(@Valid @RequestBody ProductDto productDto) {
        try {
            ProductDto product = productService.createProduct(productDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly added product", product);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @PutMapping("/updateProduct")
    public ApiResponse<ProductDto> updateProduct(@Valid @RequestBody ProductDto productDto) {
        try {
            ProductDto updatedProduct = productService.updateProduct(productDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly updated product", updatedProduct);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<ProductDto> deleteProduct(@PathVariable UUID id) {
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly deleted product", productService.deleteProduct(id));
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
