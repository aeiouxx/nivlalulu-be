package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.repository.lInvoiceRepository;
import com.nivlalulu.nnpro.repository.IProductRepository;
import com.nivlalulu.nnpro.dto.v1.ProductDto;
import com.nivlalulu.nnpro.model.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final IProductRepository IProductRepository;

    private final lInvoiceRepository lInvoiceRepository;

    public ProductDto createProduct(ProductDto productDto) {
        Optional<InvoiceItem> isProductExisting = IProductRepository.findProductByNameAndUnitPrice(productDto.getName(), productDto.getPrice());
        if (isProductExisting.isPresent()) {
            return duplicityCheck(isProductExisting.get(), productDto) ?
                    MappingService.convertToDto(isProductExisting.get()) :
                    MappingService.convertToDto(IProductRepository.save(isProductExisting.get()));
        }

        InvoiceItem invoiceItem = new InvoiceItem(productDto.getName(), productDto.getQuantity(), productDto.getPrice(),
                productDto.getTax(), productDto.getTotal());
        return MappingService.convertToDto(IProductRepository.save(invoiceItem));
    }

    public ProductDto updateProduct(ProductDto productDto) {
        InvoiceItem invoiceItem = IProductRepository.findById(productDto.getId())
                .orElseThrow(() -> new RuntimeException(String.format("Product with id %s doesn't exist, can't be updated",
                        productDto.getId())));

        invoiceItem.setName(productDto.getName());
        invoiceItem.setUnitPrice(productDto.getPrice());
        invoiceItem.setTaxPrice(productDto.getTax());
        invoiceItem.setTotalPrice(productDto.getTotal());
        invoiceItem.setQuantity(productDto.getQuantity());

        return MappingService.convertToDto(IProductRepository.save(invoiceItem));
    }

    public ProductDto deleteProduct(UUID id) {
        InvoiceItem invoiceItem = IProductRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Product with id %s doesn't exist, can't be deleted", id)));

        List<Invoice> listWhichContainsProduct = lInvoiceRepository.findAllByProductListContains(invoiceItem);

        if (!listWhichContainsProduct.isEmpty()) {
            throw new RuntimeException(String.format("Product with id %s can't be deleted, is in invoices", id));
        }

        IProductRepository.delete(invoiceItem);
        return MappingService.convertToDto(invoiceItem);
    }

    public List<ProductDto> findAllByPrice(BigDecimal price) {
        return IProductRepository.findAllByUnitPrice(price).stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<ProductDto> findAllByNameContaining(String name) {
        return IProductRepository.findAllByNameContaining(name).stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<InvoiceItem> findProductsByIds(List<UUID> ids) {
        return IProductRepository.findByIdIn(ids);
    }

    public Optional<InvoiceItem> findProductById(UUID id) {
        return IProductRepository.findById(id);
    }

    protected boolean duplicityCheck(InvoiceItem invoiceItem, ProductDto productDto) {
        boolean isNameMatching = invoiceItem.getName().equals(productDto.getName());
        boolean isNamePrice = invoiceItem.getUnitPrice().equals(productDto.getPrice());
        return isNameMatching && isNamePrice;
    }
}
