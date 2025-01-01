package com.nivlalulu.nnpro;

import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.repository.IInvoiceItemRepository;
import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import com.nivlalulu.nnpro.service.impl.InvoiceItemService;
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
    private InvoiceItemService invoiceItemService;
    @Autowired
    private IInvoiceItemRepository IInvoiceRepository;
    @Autowired
    private GenericModelMapper mapper;

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
        InvoiceItem productDto = new InvoiceItem("Test Product", 10, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
        InvoiceItemDto createdProduct = invoiceItemService.createInvoiceItem(mapper.convertToDto(productDto));

        assertNotNull(createdProduct);
        assertEquals(productDto.getName(), createdProduct.getName());
        assertEquals(productDto.getUnitPrice(), createdProduct.getUnitPrice());
    }

    @Test
    public void duplicatedProduct() {
        InvoiceItem newProduct = new InvoiceItem("Test Product", 10, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
        InvoiceItemDto newProductDto = invoiceItemService.createInvoiceItem(mapper.convertToDto(newProduct));

        InvoiceItem duplicatedProduct = new InvoiceItem("Test Product", 10, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
        InvoiceItemDto existingProduct = invoiceItemService.createInvoiceItem(mapper.convertToDto(duplicatedProduct));

        assertNotNull(newProductDto);
        assertEquals(newProductDto.getId(), existingProduct.getId());
        assertEquals(newProductDto.getName(), existingProduct.getName());
        assertEquals(newProductDto.getUnitPrice(), existingProduct.getUnitPrice());
    }

    @Test
    public void testUpdateProduct() {
        InvoiceItem product = new InvoiceItem("Old Product", 5, new BigDecimal("10.00"), new BigDecimal("1.00"), new BigDecimal("11.00"));
        IInvoiceRepository.save(product);

        InvoiceItemDto updateDto = new InvoiceItemDto(product.getId(), "Updated Product", 7, new BigDecimal("15.00"), new BigDecimal("1.50"), new BigDecimal("16.50"), null);
        InvoiceItemDto updatedProduct = invoiceItemService.updateInvoiceItem(updateDto);

        assertNotNull(updatedProduct);
        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals(new BigDecimal("15.00"), updatedProduct.getUnitPrice());
    }

    @Test
    public void testDeleteProduct_ProductExists() {
        InvoiceItem product = new InvoiceItem("Test Product", 10, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
        IInvoiceRepository.save(product);

        InvoiceItemDto deletedProduct = invoiceItemService.deleteInvoiceItem(product.getId());

        assertNotNull(deletedProduct);
        assertEquals(product.getName(), deletedProduct.getName());
        assertTrue(IInvoiceRepository.findById(product.getId()).isEmpty());
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
        InvoiceItem product1 = new InvoiceItem("Product 1", 5, new BigDecimal("10.00"), new BigDecimal("1.00"), new BigDecimal("11.00"));
        InvoiceItem product2 = new InvoiceItem("Product 2", 3, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));

        IInvoiceRepository.save(product1);
        IInvoiceRepository.save(product2);

        List<InvoiceItemDto> products = invoiceItemService.findAllByPrice(new BigDecimal("10.00"));

        assertEquals(1, products.size());
        assertEquals("Product 1", products.get(0).getName());
    }

}
