package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.repository.lInvoiceRepository;
import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.ProductDto;

import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.Product;
import com.nivlalulu.nnpro.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final lInvoiceRepository lInvoiceRepository;

    private final ProductService productService;

    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {

        Set<Product> productList = invoiceDto.getProducts().stream().map(MappingService::convertToEntity).collect(Collectors.toSet());
        User customer = MappingService.convertToEntity(invoiceDto.getCustomer());
        User supplier = MappingService.convertToEntity(invoiceDto.getSupplier());

        Invoice invoice = new Invoice(invoiceDto.getIssueDate(), invoiceDto.getDueDate(),
                invoiceDto.getPaymentMethod(), productList, customer, supplier);
        productList.forEach(product -> productService.createProduct(MappingService.convertToDto(product)));
        return MappingService.convertToDto(lInvoiceRepository.save(invoice));
    }

    public InvoiceDto updateInvoice(InvoiceDto invoiceUpdated) {
        Invoice invoice = checkIfInvoiceExisting(invoiceUpdated.getId());

        invoice.setProductList(invoiceUpdated.getProducts().stream().map(MappingService::convertToEntity).collect(Collectors.toSet()));
        invoice.setExpiration(invoiceUpdated.getDueDate());

        return MappingService.convertToDto(lInvoiceRepository.save(invoice));
    }

    public InvoiceDto deleteInvoice(UUID id) {
        Invoice invoice = checkIfInvoiceExisting(id);
        lInvoiceRepository.delete(invoice);
        return MappingService.convertToDto(invoice);
    }

    public InvoiceDto addProductToInvoice(UUID invoiceId, List<ProductDto> productsIds) {
        Invoice existingInvoice = checkIfInvoiceExisting(invoiceId);
        existingInvoice.getProductList().addAll(validateProductsForInvoice(productsIds));
        return updateInvoice(MappingService.convertToDto(existingInvoice));
    }

    public InvoiceDto removeProductFromInvoice(UUID invoiceId, List<ProductDto> productsIds) {
        Invoice existingInvoice = checkIfInvoiceExisting(invoiceId);
        existingInvoice.getProductList().removeAll(validateProductsForInvoice(productsIds));
        return updateInvoice(MappingService.convertToDto(existingInvoice));
    }


    public InvoiceDto findInvoiceDtoById(UUID id) {
        Invoice invoice = checkIfInvoiceExisting(id);
        return MappingService.convertToDto(invoice);
    }

    protected Optional<Invoice> findInvoiceById(UUID id) {
        return lInvoiceRepository.findById(id);
    }

    public List<InvoiceDto> findAllInvoices() {
        return lInvoiceRepository.findAll().stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<Invoice> findAllContainsProduct(Product product) {
        return lInvoiceRepository.findAllByProductListContains(product);
    }

    public List<Product> validateProductsForInvoice(List<ProductDto> productsIds) {
        List<Product> products = new ArrayList<>();
        for (ProductDto productId : productsIds) {
            Optional<Product> existingProduct = productService.findProductById(productId.getId());
            if (existingProduct.isEmpty()) {
                throw new RuntimeException(String.format("Product id %s can't be finded", productId));
            }
            products.add(existingProduct.get());
        }
        return products;
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
