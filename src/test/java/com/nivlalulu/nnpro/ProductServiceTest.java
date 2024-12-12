package com.nivlalulu.nnpro;

import com.nivlalulu.nnpro.dao.ProductRepository;
import com.nivlalulu.nnpro.dto.ProductDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.Product;
import com.nivlalulu.nnpro.service.ProductService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testCreateProduct_NewProduct() {
        ProductDto productDto = new ProductDto(UUID.randomUUID(), "Test Product", 10, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));

        ProductDto createdProduct = productService.createProduct(productDto);

        assertNotNull(createdProduct);
        assertEquals(productDto.getName(), createdProduct.getName());
        assertEquals(productDto.getPrice(), createdProduct.getPrice());
    }

    @Test
    void testCreateProduct_ExistingProduct() {
        Product existingProduct = new Product("Test Product", 10, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
        productRepository.save(existingProduct);

        ProductDto productDto = new ProductDto(existingProduct.getId(), "Test Product", 10, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
        ProductDto createdProduct = productService.createProduct(productDto);

        assertNotNull(createdProduct);
        assertEquals(existingProduct.getName(), createdProduct.getName());
        assertEquals(existingProduct.getPrice(), createdProduct.getPrice());
    }

    @Test
    void testUpdateProduct() {
        Product product = new Product("Old Product", 5, new BigDecimal("10.00"), new BigDecimal("1.00"), new BigDecimal("11.00"));
        productRepository.save(product);

        ProductDto updateDto = new ProductDto(product.getId(), "Updated Product", 7, new BigDecimal("15.00"), new BigDecimal("1.50"), new BigDecimal("16.50"));
        ProductDto updatedProduct = productService.updateProduct(updateDto);

        assertNotNull(updatedProduct);
        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals(new BigDecimal("15.00"), updatedProduct.getPrice());
    }

    @Test
    void testDeleteProduct_ProductExists() {
        Product product = new Product("Test Product", 10, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
        productRepository.save(product);

        ProductDto deletedProduct = productService.deleteProduct(product.getId());

        assertNotNull(deletedProduct);
        assertEquals(product.getName(), deletedProduct.getName());
        assertTrue(productRepository.findById(product.getId()).isEmpty());
    }

    @Test
    void testDeleteProduct_ProductInInvoice() {
        Product product = new Product("Test Product", 10, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
        productRepository.save(product);

//        Invoice invoice = new Invoice();
//        invoiceService.save(invoice);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.deleteProduct(product.getId()));
        assertEquals(String.format("Product with id %s can't be deleted, is in invoices", product.getId()), exception.getMessage());
    }

    @Test
    void testFindAllByPrice() {
        Product product1 = new Product("Product 1", 5, new BigDecimal("10.00"), new BigDecimal("1.00"), new BigDecimal("11.00"));
        Product product2 = new Product("Product 2", 3, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));

        productRepository.save(product1);
        productRepository.save(product2);

        List<ProductDto> products = productService.findAllByPrice(new BigDecimal("10.00"));

        assertEquals(1, products.size());
        assertEquals("Product 1", products.get(0).getName());
    }

}
