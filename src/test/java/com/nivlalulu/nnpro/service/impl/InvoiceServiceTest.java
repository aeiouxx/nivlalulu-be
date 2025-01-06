package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class InvoiceServiceTest {
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private GenericModelMapper mapper;


}

