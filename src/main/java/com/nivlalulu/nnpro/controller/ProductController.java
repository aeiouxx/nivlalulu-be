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

    @PostMapping(name = "/saveProduct")
    public ApiResponse<ProductDto> saveProduct(@RequestBody ProductDto productDto) {
        try {
            ProductDto product = productService.createProduct(productDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly added product", product);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @PutMapping(name = "/updateProduct")
    public ApiResponse<ProductDto> updateProduct(@RequestBody ProductDto productDto) {
        try {
            ProductDto updatedProduct = productService.updateProduct(productDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly updated product", updatedProduct);
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

    @DeleteMapping(name = "/{id}")
    public ApiResponse<ProductDto> deleteProduct(@PathVariable UUID id) {
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Successfuly deleted product", productService.deleteProduct(id));
        } catch (RuntimeException ex) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        }
    }

}
