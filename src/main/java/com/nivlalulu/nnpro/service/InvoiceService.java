package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dao.InvoiceRepository;
import com.nivlalulu.nnpro.dto.InvoiceDto;
import com.nivlalulu.nnpro.dto.ProductDto;

import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    private final ProductService productService;

    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {

        Set<InvoiceItem> invoiceItemList = invoiceDto.getProducts().stream().map(MappingService::convertToEntity).collect(Collectors.toSet());
        User customer = MappingService.convertToEntity(invoiceDto.getCustomer());
        User supplier = MappingService.convertToEntity(invoiceDto.getSupplier());

        Invoice invoice = new Invoice(invoiceDto.getIssueDate(), invoiceDto.getDueDate(),
                invoiceDto.getPaymentMethod(), invoiceItemList, customer, supplier);
        invoiceItemList.forEach(product -> productService.createProduct(MappingService.convertToDto(product)));
        return MappingService.convertToDto(invoiceRepository.save(invoice));
    }

    public InvoiceDto updateInvoice(InvoiceDto invoiceUpdated) {
        Invoice invoice = checkIfInvoiceExisting(invoiceUpdated.getId());

        invoice.setInvoiceItemList(invoiceUpdated.getProducts().stream().map(MappingService::convertToEntity).collect(Collectors.toSet()));
        invoice.setExpiration(invoiceUpdated.getDueDate());

        return MappingService.convertToDto(invoiceRepository.save(invoice));
    }

    public InvoiceDto deleteInvoice(UUID id) {
        Invoice invoice = checkIfInvoiceExisting(id);
        invoiceRepository.delete(invoice);
        return MappingService.convertToDto(invoice);
    }

    public InvoiceDto addProductToInvoice(UUID invoiceId, List<ProductDto> productsIds) {
        Invoice existingInvoice = checkIfInvoiceExisting(invoiceId);
        existingInvoice.getInvoiceItemList().addAll(validateProductsForInvoice(productsIds));
        return updateInvoice(MappingService.convertToDto(existingInvoice));
    }

    public InvoiceDto removeProductFromInvoice(UUID invoiceId, List<ProductDto> productsIds) {
        Invoice existingInvoice = checkIfInvoiceExisting(invoiceId);
        existingInvoice.getInvoiceItemList().removeAll(validateProductsForInvoice(productsIds));
        return updateInvoice(MappingService.convertToDto(existingInvoice));
    }


    public InvoiceDto findInvoiceDtoById(UUID id) {
        Invoice invoice = checkIfInvoiceExisting(id);
        return MappingService.convertToDto(invoice);
    }

    protected Optional<Invoice> findInvoiceById(UUID id) {
        return invoiceRepository.findById(id);
    }

    public List<InvoiceDto> findAllInvoices() {
        return invoiceRepository.findAll().stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<Invoice> findAllContainsProduct(InvoiceItem invoiceItem) {
        return invoiceRepository.findAllByProductListContains(invoiceItem);
    }

    public List<InvoiceItem> validateProductsForInvoice(List<ProductDto> productsIds) {
        List<InvoiceItem> invoiceItems = new ArrayList<>();
        for (ProductDto productId : productsIds) {
            Optional<InvoiceItem> existingProduct = productService.findProductById(productId.getId());
            if (existingProduct.isEmpty()) {
                throw new RuntimeException(String.format("Product id %s can't be finded", productId));
            }
            invoiceItems.add(existingProduct.get());
        }
        return invoiceItems;
    }

    public Invoice checkIfInvoiceExisting(UUID invoiceId) {
        Optional<Invoice> existingInvoice = findInvoiceById(invoiceId);
        if (existingInvoice.isEmpty()) {
            throw new RuntimeException(String.format("Invoice with id %s doens't exists", invoiceId));
        } else {
            return existingInvoice.get();
        }
    }

}
