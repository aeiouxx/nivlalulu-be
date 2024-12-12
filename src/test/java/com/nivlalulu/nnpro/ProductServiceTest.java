package com.nivlalulu.nnpro;

import com.nivlalulu.nnpro.repository.IProductRepository;
import com.nivlalulu.nnpro.dto.v1.ProductDto;
import com.nivlalulu.nnpro.model.Product;
import com.nivlalulu.nnpro.service.impl.MappingService;
import com.nivlalulu.nnpro.service.impl.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private IProductRepository IProductRepository;

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_pass")
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
    }


    @Test
    public void newProduct() {
        Product productDto = new Product("Test Product", 10, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
        ProductDto createdProduct = productService.createProduct(MappingService.convertToDto(productDto));

        assertNotNull(createdProduct);
        assertEquals(productDto.getName(), createdProduct.getName());
        assertEquals(productDto.getPrice(), createdProduct.getPrice());
    }

    @Test
    public void duplicatedProduct() {
        Product newProduct = new Product("Test Product", 10, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
        ProductDto newProductDto = productService.createProduct(MappingService.convertToDto(newProduct));

        Product duplicatedProduct = new Product("Test Product", 10, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
        ProductDto existingProduct = productService.createProduct(MappingService.convertToDto(duplicatedProduct));

        assertNotNull(newProductDto);
        assertEquals(newProductDto.getId(), existingProduct.getId());
        assertEquals(newProductDto.getName(), existingProduct.getName());
        assertEquals(newProductDto.getPrice(), existingProduct.getPrice());
    }

    @Test
    public void testUpdateProduct() {
        Product product = new Product("Old Product", 5, new BigDecimal("10.00"), new BigDecimal("1.00"), new BigDecimal("11.00"));
        IProductRepository.save(product);

        ProductDto updateDto = new ProductDto(product.getId(), "Updated Product", 7, new BigDecimal("15.00"), new BigDecimal("1.50"), new BigDecimal("16.50"));
        ProductDto updatedProduct = productService.updateProduct(updateDto);

        assertNotNull(updatedProduct);
        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals(new BigDecimal("15.00"), updatedProduct.getPrice());
    }

    @Test
    public void testDeleteProduct_ProductExists() {
        Product product = new Product("Test Product", 10, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
        IProductRepository.save(product);

        ProductDto deletedProduct = productService.deleteProduct(product.getId());

        assertNotNull(deletedProduct);
        assertEquals(product.getName(), deletedProduct.getName());
        assertTrue(IProductRepository.findById(product.getId()).isEmpty());
    }

//    @Test
//    public void testDeleteProduct_ProductInInvoice() {
//        Product product = new Product("Test Product", 10, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
//        productRepository.save(product);
//
//        //TODO až budou invoice => čekám na vladosákovy usery
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.deleteProduct(product.getId()));
//        assertEquals(String.format("Product with id %s can't be deleted, is in invoices", product.getId()), exception.getMessage());
//    }

    @Test
    public void testFindAllByPrice() {
        Product product1 = new Product("Product 1", 5, new BigDecimal("10.00"), new BigDecimal("1.00"), new BigDecimal("11.00"));
        Product product2 = new Product("Product 2", 3, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));

        IProductRepository.save(product1);
        IProductRepository.save(product2);

        List<ProductDto> products = productService.findAllByPrice(new BigDecimal("10.00"));

        assertEquals(1, products.size());
        assertEquals("Product 1", products.get(0).getName());
    }

}
