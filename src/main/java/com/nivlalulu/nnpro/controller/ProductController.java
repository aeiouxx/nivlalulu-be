package com.nivlalulu.nnpro.controller;

import com.nivlalulu.nnpro.dto.ProductDto;
import com.nivlalulu.nnpro.model.ApiResponse;
import com.nivlalulu.nnpro.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ApiResponse<ProductDto> saveProduct(@RequestBody ProductDto productDto) {
        ProductDto product = productService.createProduct(productDto);
        return product != null ?
                new ApiResponse<>(HttpStatus.OK.value(), "Successfuly added product", product) :
                new ApiResponse<>(HttpStatus.OK.value(), "Already exisiting product", null);
    }

    @PutMapping
    public ApiResponse<ProductDto> updateProduct(@RequestBody ProductDto productDto) {
        try {
            ProductDto updatedProduct = productService.updateProduct(productDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly updated product", updatedProduct);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.OK.value(), "Product not found, can't be updated", null);
        }
    }

    @DeleteMapping
    public ApiResponse<ProductDto> deleteProduct(@PathVariable UUID productId) {
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly deleted product", productService.deleteProduct(productId));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.OK.value(), "Product can't be deleted", null);
        }
    }

}
