package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dao.InvoiceRepository;
import com.nivlalulu.nnpro.dto.InvoiceDto;
import com.nivlalulu.nnpro.dto.ProductDto;

import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.Product;
import com.nivlalulu.nnpro.model.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;


    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {
        validateInvoice(invoiceDto);

        List<Product> productList = invoiceDto.getProducts().stream().map(MappingService::convertToEntity).collect(Collectors.toList());
        User customer = MappingService.convertToEntity(invoiceDto.getCustomer());
        User supplier = MappingService.convertToEntity(invoiceDto.getSupplier());

        Invoice invoice = new Invoice(invoiceDto.getCompanyName(), invoiceDto.getCreated(), invoiceDto.getExpiration(),
                invoiceDto.getPaymentMethod(), productList, customer, supplier);
        return MappingService.convertToDto(invoiceRepository.save(invoice));
    }

    public InvoiceDto updateInvoice(InvoiceDto invoiceUpdated) {
        Invoice invoice = checkIfInvoiceExisting(invoiceUpdated.getId());
        validateInvoice(invoiceUpdated);

        invoice.setProductList(invoiceUpdated.getProducts().stream().map(MappingService::convertToEntity).collect(Collectors.toList()));
        invoice.setCompanyName(invoiceUpdated.getCompanyName());
        invoice.setExpiration(invoiceUpdated.getExpiration());

        return MappingService.convertToDto(invoiceRepository.save(invoice));
    }

    public InvoiceDto deleteInvoice(UUID id) {
        Invoice invoice = checkIfInvoiceExisting(id);
        invoiceRepository.delete(invoice);
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
        return invoiceRepository.findById(id);
    }

    public List<InvoiceDto> findAllInvoices() {
        return invoiceRepository.findAll().stream().map(MappingService::convertToDto).collect(Collectors.toList());
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

    public void validateInvoice(InvoiceDto invoiceDto) {
        if (invoiceDto.getTaxId() == null) {
            throw new RuntimeException("Tax id is null");
        }
        if (invoiceDto.getCreated() == null) {
            throw new RuntimeException("Invoice creation is null");
        }
        if (invoiceDto.getExpiration() == null) {
            throw new RuntimeException("Invoice expiration is null");
        }
        if (invoiceDto.getCustomer() == null) {
            throw new RuntimeException("User is null");
        }

        userService.validateUser(invoiceDto.getSupplier());
        userService.validateUser(invoiceDto.getCustomer());

        if (!invoiceDto.getProducts().isEmpty()) {
            for (ProductDto product : invoiceDto.getProducts()) {
                productService.validateProduct(product);
            }
        }
    }

}
