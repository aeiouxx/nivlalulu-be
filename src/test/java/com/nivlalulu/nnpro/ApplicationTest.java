package com.nivlalulu.nnpro;

import com.nivlalulu.nnpro.dto.ProductDto;
import com.nivlalulu.nnpro.service.InvoiceService;
import com.nivlalulu.nnpro.service.ProductService;
import io.jsonwebtoken.lang.Assert;
import org.hibernate.cfg.Environment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import java.math.BigDecimal;
import java.util.Arrays;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationTest {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ProductService productService;

    @Test
    public void contextLoads() {

    }

    @Test
    public void testCorrectSaveProduct() {

        ProductDto newProduct = new ProductDto();
        newProduct.setName("testProduct");
        newProduct.setQuantity(2);
        newProduct.setPrice(BigDecimal.valueOf(100));
        newProduct.setTax(BigDecimal.valueOf(21));
        newProduct.setTotal(BigDecimal.valueOf(121));

        ProductDto productCreated = productService.createProduct(newProduct);

        Assert.isTrue(productCreated != null, "Product succesfully created");
    }
}
