package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.mapping.IGenericMapper;
import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import com.nivlalulu.nnpro.repository.IInvoiceRepository;
import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.ProductDto;

import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.Product;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.service.IInvoiceService;
import com.nivlalulu.nnpro.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InvoiceService implements IInvoiceService {
    private final IInvoiceRepository invoiceRepository;
    private final ProductService productService;
    private final GenericModelMapper mapper;

    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {

        Set<Product> productList = invoiceDto.getProducts().stream().map(mapper::convertToEntity).collect(Collectors.toSet());
        User customer = mapper.convertToEntity(invoiceDto.getCustomer());
        User supplier = mapper.convertToEntity(invoiceDto.getSupplier());

        Invoice invoice = new Invoice(invoiceDto.getIssueDate(), invoiceDto.getDueDate(),
                invoiceDto.getPaymentMethod(), productList, customer, supplier);
        productList.forEach(product -> productService.createProduct(mapper.convertToDto(product)));
        return mapper.convertToDto(invoiceRepository.save(invoice));
    }

    public InvoiceDto updateInvoice(InvoiceDto invoiceUpdated) {
        Invoice invoice = checkIfInvoiceExisting(invoiceUpdated.getId());

        invoice.setProductList(invoiceUpdated.getProducts().stream().map(mapper::convertToEntity).collect(Collectors.toSet()));
        invoice.setExpiration(invoiceUpdated.getDueDate());

        return mapper.convertToDto(invoiceRepository.save(invoice));
    }

    public InvoiceDto deleteInvoice(UUID id) {
        Invoice invoice = checkIfInvoiceExisting(id);
        invoiceRepository.delete(invoice);
        return mapper.convertToDto(invoice);
    }

    public InvoiceDto addProductToInvoice(UUID invoiceId, List<ProductDto> productsIds) {
        Invoice existingInvoice = checkIfInvoiceExisting(invoiceId);
        existingInvoice.getProductList().addAll(validateProductsForInvoice(productsIds));
        return updateInvoice(mapper.convertToDto(existingInvoice));
    }

    public InvoiceDto removeProductFromInvoice(UUID invoiceId, List<ProductDto> productsIds) {
        Invoice existingInvoice = checkIfInvoiceExisting(invoiceId);
        existingInvoice.getProductList().removeAll(validateProductsForInvoice(productsIds));
        return updateInvoice(mapper.convertToDto(existingInvoice));
    }


    public InvoiceDto findInvoiceDtoById(UUID id) {
        Invoice invoice = checkIfInvoiceExisting(id);
        return mapper.convertToDto(invoice);
    }

    protected Optional<Invoice> findInvoiceById(UUID id) {
        return invoiceRepository.findById(id);
    }

    public List<InvoiceDto> findAllInvoices() {
        return invoiceRepository
                .findAll()
                .stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toList());
    }

    public List<Invoice> findAllContainsProduct(Product product) {
        return invoiceRepository.findAllByProductListContains(product);
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
