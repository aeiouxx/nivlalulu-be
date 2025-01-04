package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.repository.IInvoiceItemRepository;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.service.IInvoiceItemService;
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
public class InvoiceItemServiceTest {

    @Autowired
    private IInvoiceItemService invoiceService;

    @Autowired
    private IInvoiceItemRepository IInvoiceItemRepository;

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
    public void testFindAllByPrice() {
        InvoiceItem invoiceItem1 = new InvoiceItem("Product 1", 5, new BigDecimal("10.00"),
                new BigDecimal("1.00"), new BigDecimal("11.00"));
        InvoiceItem invoiceItem2 = new InvoiceItem("Product 2", 3, new BigDecimal("20.00"),
                new BigDecimal("2.00"), new BigDecimal("22.00"));

        IInvoiceItemRepository.save(invoiceItem1);
        IInvoiceItemRepository.save(invoiceItem2);

        List<InvoiceItemDto> products = invoiceService.findByPrice(new BigDecimal("10.00"));

        assertEquals(1, products.size());
        assertEquals("Product 1", products.get(0).getName());
    }

}
