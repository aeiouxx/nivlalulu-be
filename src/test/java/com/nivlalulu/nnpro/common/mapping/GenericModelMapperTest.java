package com.nivlalulu.nnpro.common.mapping;

import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import com.nivlalulu.nnpro.configuration.ModelMapperConfiguration;
import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.dto.v1.PartySnapshotDto;
import com.nivlalulu.nnpro.enums.PaymentMethod;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.InvoiceItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GenericModelMapperTest {
    private static final UUID INVOICE_ID = UUID.randomUUID();
    private static final UUID INVOICE_ITEM_ID = UUID.randomUUID();

    private GenericModelMapper mapper;

    @BeforeEach
    void setUp() {
        ModelMapperConfiguration modelMapperConfiguration = new ModelMapperConfiguration();
        mapper = new GenericModelMapper(modelMapperConfiguration.modelMapper());
    }

    @Test
    public void testConvertToDto() {
        Invoice invoice = sampleInvoice();
        InvoiceDto invoiceDto = mapper.convertToDto(invoice);
        assertNotNull(invoiceDto);
        assertEquals(invoice.getId(), invoiceDto.getId());
        assertEquals(invoice.getCreatedAt(), invoiceDto.getCreatedAt());
        assertEquals(invoice.getExpiresAt(), invoiceDto.getExpiresAt());
        assertEquals(invoice.getPaymentMethod(), invoiceDto.getPaymentMethod());
        assertEquals(invoice.getVariableSymbol(), invoiceDto.getVariableSymbol());
        assertEquals(invoice.getSupplierName(), invoiceDto.getSupplier().getName());
        assertEquals(invoice.getSupplierAddress(), invoiceDto.getSupplier().getAddress());
        assertEquals(invoice.getSupplierCountry(), invoiceDto.getSupplier().getCountry());
        assertEquals(invoice.getSupplierIcTax(), invoiceDto.getSupplier().getIcTax());
        assertEquals(invoice.getSupplierDicTax(), invoiceDto.getSupplier().getDicTax());
        assertEquals(invoice.getSupplierTelephone(), invoiceDto.getSupplier().getTelephone());
        assertEquals(invoice.getSupplierEmail(), invoiceDto.getSupplier().getEmail());
        assertEquals(invoice.getCustomerName(), invoiceDto.getCustomer().getName());
        assertEquals(invoice.getCustomerAddress(), invoiceDto.getCustomer().getAddress());
        assertEquals(invoice.getCustomerCountry(), invoiceDto.getCustomer().getCountry());
        assertEquals(invoice.getCustomerIcTax(), invoiceDto.getCustomer().getIcTax());
        assertEquals(invoice.getCustomerDicTax(), invoiceDto.getCustomer().getDicTax());
        assertEquals(invoice.getItems().size(), invoiceDto.getItems().size());
    }

    @Test
    public void testConvertToEntity() {
        InvoiceDto invoiceDto = sampleInvoiceDto();
        Invoice invoice = mapper.convertToEntity(invoiceDto);
        assertNotNull(invoice);
        assertEquals(invoiceDto.getId(), invoice.getId());
        assertEquals(invoiceDto.getCreatedAt(), invoice.getCreatedAt());
        assertEquals(invoiceDto.getExpiresAt(), invoice.getExpiresAt());
        assertEquals(invoiceDto.getPaymentMethod(), invoice.getPaymentMethod());
        assertEquals(invoiceDto.getVariableSymbol(), invoice.getVariableSymbol());
        assertEquals(invoiceDto.getContact(), invoice.getContact());
        assertEquals(invoiceDto.getSupplier().getName(), invoice.getSupplierName());
        assertEquals(invoiceDto.getSupplier().getAddress(), invoice.getSupplierAddress());
        assertEquals(invoiceDto.getSupplier().getCountry(), invoice.getSupplierCountry());
        assertEquals(invoiceDto.getSupplier().getIcTax(), invoice.getSupplierIcTax());
        assertEquals(invoiceDto.getSupplier().getDicTax(), invoice.getSupplierDicTax());
        assertEquals(invoiceDto.getSupplier().getTelephone(), invoice.getSupplierTelephone());
        assertEquals(invoiceDto.getSupplier().getEmail(), invoice.getSupplierEmail());
        assertEquals(invoiceDto.getCustomer().getName(), invoice.getCustomerName());
        assertEquals(invoiceDto.getCustomer().getAddress(), invoice.getCustomerAddress());
        assertEquals(invoiceDto.getCustomer().getCountry(), invoice.getCustomerCountry());
        assertEquals(invoiceDto.getCustomer().getIcTax(), invoice.getCustomerIcTax());
        assertEquals(invoiceDto.getCustomer().getDicTax(), invoice.getCustomerDicTax());
        assertEquals(invoiceDto.getItems().size(), invoice.getItems().size());
    }


    @Test
    public void convertToEntityWithNullValues() {
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setId(INVOICE_ID);
        invoiceDto.setCreatedAt(Instant.now());
        invoiceDto.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        invoiceDto.setPaymentMethod(PaymentMethod.P);
        invoiceDto.setVariableSymbol("123456");

        Invoice invoice = mapper.convertToEntity(invoiceDto);


        assertNotNull(invoiceDto);
        assertEquals(invoice.getId(), invoiceDto.getId());
        assertEquals(invoice.getCreatedAt(), invoiceDto.getCreatedAt());
        assertEquals(invoice.getExpiresAt(), invoiceDto.getExpiresAt());
        assertEquals(invoice.getPaymentMethod(), invoiceDto.getPaymentMethod());
        assertEquals(invoice.getVariableSymbol(), invoiceDto.getVariableSymbol());
        assertNull(invoiceDto.getSupplier());
        assertNull(invoiceDto.getCustomer());
        assertNull(invoiceDto.getItems());
    }


    private Invoice sampleInvoice() {
        Invoice invoice = new Invoice();
        invoice.setId(INVOICE_ID);
        invoice.setCreatedAt(Instant.now());
        invoice.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        invoice.setPaymentMethod(PaymentMethod.P);
        invoice.setVariableSymbol("123456");
        invoice.setSupplierName("Supplier Name");
        invoice.setSupplierAddress("Supplier Address");
        invoice.setSupplierCountry("Supplier Country");
        invoice.setSupplierIcTax("Supplier IC Tax");
        invoice.setSupplierDicTax("Supplier DIC Tax");
        invoice.setSupplierTelephone("Supplier Telephone");
        invoice.setSupplierEmail("Supplier Email");
        invoice.setCustomerName("Customer Name");
        invoice.setCustomerAddress("Customer Address");
        invoice.setCustomerCountry("Customer Country");
        invoice.setCustomerIcTax("Customer IC Tax");
        invoice.setCustomerDicTax("Customer DIC Tax");
        invoice.setContact("John Doe");

        Set<InvoiceItem> items = new HashSet<>();
        InvoiceItem item = new InvoiceItem();
        item.setId(INVOICE_ITEM_ID);
        item.setQuantity(1);
        item.setUnitPrice(BigDecimal.valueOf(100));
        items.add(item);
        invoice.setItems(items);

        return invoice;
    }

    private InvoiceDto sampleInvoiceDto() {
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setId(INVOICE_ID);
        invoiceDto.setCreatedAt(Instant.now());
        invoiceDto.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        invoiceDto.setPaymentMethod(PaymentMethod.P);
        invoiceDto.setVariableSymbol("123456");
        invoiceDto.setContact("John Doe");

        PartySnapshotDto supplier = new PartySnapshotDto(
                "Supplier Name",
                "Supplier Address",
                "Supplier Country",
                "Supplier IC Tax",
                "Supplier DIC Tax",
                "Supplier Telephone",
                "Supplier Email"
        );
        invoiceDto.setSupplier(supplier);

        PartySnapshotDto customer = new PartySnapshotDto(
                "Customer Name",
                "Customer Address",
                "Customer Country",
                "Customer IC Tax",
                "Customer DIC Tax",
                "Customer Telephone",
                "Customer Email"
        );
        invoiceDto.setCustomer(customer);

        Set<InvoiceItemDto> items = new HashSet<>();
        InvoiceItemDto itemDto = new InvoiceItemDto();
        itemDto.setId(INVOICE_ITEM_ID);
        itemDto.setQuantity(1);
        itemDto.setUnitPrice(BigDecimal.valueOf(100));
        items.add(itemDto);
        invoiceDto.setItems(items);

        return invoiceDto;
    }
}
