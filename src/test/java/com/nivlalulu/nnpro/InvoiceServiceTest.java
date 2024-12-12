//package com.nivlalulu.nnpro;
//
//import com.nivlalulu.nnpro.repository.InvoiceRepository;
//import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
//import com.nivlalulu.nnpro.dto.v1.ProductDto;
//import com.nivlalulu.nnpro.model.Invoice;
//import com.nivlalulu.nnpro.model.Product;
//import com.nivlalulu.nnpro.model.User;
//import com.nivlalulu.nnpro.service.impl.InvoiceService;
//import com.nivlalulu.nnpro.service.impl.ProductService;
//import com.nivlalulu.nnpro.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class InvoiceServiceTest {
//
//    private InvoiceService invoiceService;
//    private InMemoryInvoiceRepository invoiceRepository;
//    private ProductService productService;
//    private UserService userService;
//
//    @Test
//    void testCreateInvoice() {
//        ProductDto productDto = new ProductDto(UUID.randomUUID(), "Test Product", 2, new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("22.00"));
//        User customer = new User(UUID.randomUUID(), "Customer Name", "customer@test.com");
//        User supplier = new User(UUID.randomUUID(), "Supplier Name", "supplier@test.com");
//
//        InvoiceDto invoiceDto = new InvoiceDto(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(30), "Bank Transfer",
//                List.of(productDto), customer, supplier);
//
//        InvoiceDto createdInvoice = invoiceService.createInvoice(invoiceDto);
//
//        assertNotNull(createdInvoice);
//        assertEquals(1, createdInvoice.getProducts().size());
//        assertEquals("Test Product", createdInvoice.getProducts().get(0).getName());
//    }
//
//    @Test
//    void testUpdateInvoice() {
//        Invoice existingInvoice = new Invoice(LocalDate.now(), LocalDate.now().plusDays(30), "Bank Transfer", new ArrayList<>(), new User(), new User());
//        invoiceRepository.save(existingInvoice);
//
//        ProductDto newProduct = new ProductDto(UUID.randomUUID(), "Updated Product", 1, new BigDecimal("10.00"), new BigDecimal("1.00"), new BigDecimal("11.00"));
//        InvoiceDto updateDto = new InvoiceDto(existingInvoice.getId(), LocalDate.now(), LocalDate.now().plusDays(30), "Cash", List.of(newProduct), new User(), new User());
//
//        InvoiceDto updatedInvoice = invoiceService.updateInvoice(updateDto);
//
//        assertNotNull(updatedInvoice);
//        assertEquals("Cash", updatedInvoice.getPaymentMethod());
//        assertEquals(1, updatedInvoice.getProducts().size());
//    }
//
//    @Test
//    void testDeleteInvoice() {
//        Invoice invoice = new Invoice(LocalDate.now(), LocalDate.now().plusDays(30), "Bank Transfer", new ArrayList<>(), new User(), new User());
//        invoiceRepository.save(invoice);
//
//        InvoiceDto deletedInvoice = invoiceService.deleteInvoice(invoice.getId());
//
//        assertNotNull(deletedInvoice);
//        assertTrue(invoiceRepository.findById(invoice.getId()).isEmpty());
//    }
//
//    @Test
//    void testAddProductToInvoice() {
//        Invoice invoice = new Invoice(LocalDate.now(), LocalDate.now().plusDays(30), "Bank Transfer", new ArrayList<>(), new User(), new User());
//        invoiceRepository.save(invoice);
//
//        ProductDto productDto = new ProductDto(UUID.randomUUID(), "New Product", 3, new BigDecimal("15.00"), new BigDecimal("1.50"), new BigDecimal("16.50"));
//
//        InvoiceDto updatedInvoice = invoiceService.addProductToInvoice(invoice.getId(), List.of(productDto));
//
//        assertNotNull(updatedInvoice);
//        assertEquals(1, updatedInvoice.getProducts().size());
//        assertEquals("New Product", updatedInvoice.getProducts().get(0).getName());
//    }
//
//    @Test
//    void testRemoveProductFromInvoice() {
//        Product product = new Product("Existing Product", 2, new BigDecimal("10.00"), new BigDecimal("1.00"), new BigDecimal("11.00"));
//        Invoice invoice = new Invoice(LocalDate.now(), LocalDate.now().plusDays(30), "Bank Transfer", new ArrayList<>(List.of(product)), new User(), new User());
//        invoiceRepository.save(invoice);
//
//        ProductDto productDto = new ProductDto(product.getId(), "Existing Product", 2, new BigDecimal("10.00"), new BigDecimal("1.00"), new BigDecimal("11.00"));
//
//        InvoiceDto updatedInvoice = invoiceService.removeProductFromInvoice(invoice.getId(), List.of(productDto));
//
//        assertNotNull(updatedInvoice);
//        assertTrue(updatedInvoice.getProducts().isEmpty());
//    }
//
//    @Test
//    void testFindAllInvoices() {
//        Invoice invoice1 = new Invoice(LocalDate.now(), LocalDate.now().plusDays(30), "Bank Transfer", new ArrayList<>(), new User(), new User());
//        Invoice invoice2 = new Invoice(LocalDate.now(), LocalDate.now().plusDays(20), "Cash", new ArrayList<>(), new User(), new User());
//
//        invoiceRepository.save(invoice1);
//        invoiceRepository.save(invoice2);
//
//        List<InvoiceDto> invoices = invoiceService.findAllInvoices();
//
//        assertEquals(2, invoices.size());
//    }
//
//    // Simulated in-memory repositories
//
//    static class InMemoryInvoiceRepository implements InvoiceRepository {
//        private final Map<UUID, Invoice> database = new HashMap<>();
//
//        @Override
//        public Optional<Invoice> findById(UUID id) {
//            return Optional.ofNullable(database.get(id));
//        }
//
//        @Override
//        public List<Invoice> findAll() {
//            return new ArrayList<>(database.values());
//        }
//
//        @Override
//        public Invoice save(Invoice invoice) {
//            if (invoice.getId() == null) {
//                invoice.setId(UUID.randomUUID());
//            }
//            database.put(invoice.getId(), invoice);
//            return invoice;
//        }
//
//        @Override
//        public void delete(Invoice invoice) {
//            database.remove(invoice.getId());
//        }
//
//        @Override
//        public List<Invoice> findAllByProductListContains(Product product) {
//            return database.values().stream()
//                    .filter(invoice -> invoice.getProductList().contains(product))
//                    .collect(Collectors.toList());
//        }
//    }
//}
//
