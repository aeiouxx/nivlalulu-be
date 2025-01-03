package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class InvoiceServiceTest {

    private InvoiceService invoiceService;
    private InvoiceItemService invoiceItemService;

    private GenericModelMapper mapper;


//    @Test
//    void testCreateInvoice() {
//        InvoiceItem productDto = new InvoiceItem("Test Product", 2, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
//        Party customer = new Party(null, "Customer Name", "Customer address", "Customer country", null, "85749", "897645312", "customer@test.com");
//        Party supplier = new Party(null, "Supplier Name", "Supplier address", "Supplier country", null, "857749", "817645312", "supplier@test.com");
//
////        Invoice invoiceDto = new Invoice(null, LocalDate.now().plusDays(30), "Bank Transfer", new HashSet<>(Arrays.asList(productDto)), customer, supplier);
//        Invoice invoiceDto = new Invoice(null, null, PaymentMethod.P, "45", new HashSet<>(Arrays.asList(productDto)), customer, supplier);
//
//        InvoiceDto createdInvoice = invoiceService.createInvoice(mapper.convertToDto(invoiceDto));
//
//        assertNotNull(createdInvoice);
//        assertEquals(1, createdInvoice.getProducts().size());
//        assertEquals("Test Product", createdInvoice.getProducts().get(0).getName());
//    }
//
//    @Test
//    void testUpdateInvoice() {
////        InvoiceItem productDto = new InvoiceItem("Test Product", 2, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
////        Party customer = new Party(null, "Customer Name", "Customer address", "Customer country", null, "85749", "897645312", "customer@test.com");
////        Party supplier = new Party(null, "Supplier Name", "Supplier address", "Supplier country", null, "857749", "817645312", "supplier@test.com");
////
////        InvoiceDto createdInvoice = invoiceService.createInvoice(MappingService.convertToDto(invoiceDto));
////
////        ProductDto newProduct = new ProductDto(UUID.randomUUID(), "Updated Product", 1, new BigDecimal("10.00"), new BigDecimal("1.00"), new BigDecimal("11.00"));
////        InvoiceDto updateDto = new InvoiceDto(existingInvoice.getId(), LocalDate.now(), LocalDate.now().plusDays(30), "Cash", List.of(newProduct), new User(), new User());
////
////        InvoiceDto updatedInvoice = invoiceService.updateInvoice(updateDto);
////
////        assertNotNull(updatedInvoice);
////        assertEquals("Cash", updatedInvoice.getPaymentMethod());
////        assertEquals(1, updatedInvoice.getProducts().size());
//    }
//
//    @Test
//    void testDeleteInvoice() {
//        InvoiceItem invoiceItem = new InvoiceItem("Test Product", 2, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
//        Party customer = new Party(null, "Customer Name", "Customer address", "Customer country", null, "85749", "897645312", "customer@test.com");
//        Party supplier = new Party(null, "Supplier Name", "Supplier address", "Supplier country", null, "857749", "817645312", "supplier@test.com");
//
//        Invoice invoice = new Invoice(null, null, PaymentMethod.P, "45", new HashSet<>(Arrays.asList(invoiceItem)), customer, supplier);
//
//        InvoiceDto createdInvoice = invoiceService.createInvoice(mapper.convertToDto(invoice));
//
//        InvoiceDto deletedInvoice = invoiceService.deleteInvoice(invoice.getId());
//
//        assertNotNull(createdInvoice);
//        assertNotNull(deletedInvoice);
//        assertTrue(invoiceService.findInvoiceDtoById(invoice.getId()) == null);
//    }
//
//    @Test
//    void testAddProductToInvoice() {
//        Party customer = new Party(null, "Customer Name", "Customer address", "Customer country", null, "85749", "897645312", "customer@test.com");
//        Party supplier = new Party(null, "Supplier Name", "Supplier address", "Supplier country", null, "857749", "817645312", "supplier@test.com");
//
//        Invoice invoice = new Invoice(null, null, PaymentMethod.P, "45", new HashSet<>(), customer, supplier);
//
//        InvoiceDto createdInvoice = invoiceService.createInvoice(mapper.convertToDto(invoice));
//
//        InvoiceItem invoiceItem = new InvoiceItem("Test Product", 2, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
//
//        InvoiceDto updatedInvoice = invoiceService.addInvoiceItemToInvoice(invoice.getId(), List.of(mapper.convertToDto(invoiceItem)));
//
//        assertNotNull(updatedInvoice);
//        assertEquals(1, updatedInvoice.getProducts().size());
//        assertEquals("Test Product", updatedInvoice.getProducts().get(0).getName());
//    }
//
//    @Test
//    void testRemoveProductFromInvoice() {
//        InvoiceItem invoiceItem = new InvoiceItem("Test Product", 2, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
//        Party customer = new Party(null, "Customer Name", "Customer address", "Customer country", null, "85749", "897645312", "customer@test.com");
//        Party supplier = new Party(null, "Supplier Name", "Supplier address", "Supplier country", null, "857749", "817645312", "supplier@test.com");
//
//        Invoice invoice = new Invoice(null, null, PaymentMethod.P, "45", new HashSet<>(), customer, supplier);
//
//        InvoiceDto createdInvoice = invoiceService.createInvoice(mapper.convertToDto(invoice));
//
//        InvoiceDto updatedInvoice = invoiceService.removeInvoiceItemFromInvoice(invoice.getId(), List.of(mapper.convertToDto(invoiceItem)));
//
//        assertNotNull(updatedInvoice);
//        assertTrue(updatedInvoice.getProducts().isEmpty());
//    }
//
//    @Test
//    void testFindAllInvoices() {
//        InvoiceItem invoiceItem = new InvoiceItem("Test Product", 2, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
//        InvoiceItem invoiceItem_2 = new InvoiceItem("Test Product", 2, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
//        Party customer = new Party(null, "Customer Name", "Customer address", "Customer country", null, "85749", "897645312", "customer@test.com");
//        Party supplier = new Party(null, "Supplier Name", "Supplier address", "Supplier country", null, "857749", "817645312", "supplier@test.com");
//        Party supplier_2 = new Party(null, "Supplier Name", "Supplier address", "Supplier country", null, "85774749", "817645312", "supplier@test.com");
//
//        Invoice invoice_1 = new Invoice(null, null, PaymentMethod.P, "45", new HashSet<>(Arrays.asList(invoiceItem)), customer, supplier);
//        Invoice invoice_2 = new Invoice(null, null, PaymentMethod.P, "45", new HashSet<>(Arrays.asList(invoiceItem_2)), customer, supplier_2);
//
//        InvoiceDto createdInvoice_1 = invoiceService.createInvoice(mapper.convertToDto(invoice_1));
//        InvoiceDto createdInvoice_2 = invoiceService.createInvoice(mapper.convertToDto(invoice_2));
//
//        List<InvoiceDto> invoices = invoiceService.findAllInvoices();
//
//        assertNotNull(createdInvoice_1);
//        assertNotNull(createdInvoice_2);
//        assertEquals(2, invoices.size());
//    }
}

