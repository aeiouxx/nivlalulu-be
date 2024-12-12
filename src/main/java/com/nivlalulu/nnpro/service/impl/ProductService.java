package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.repository.lInvoiceRepository;
import com.nivlalulu.nnpro.repository.IProductRepository;
import com.nivlalulu.nnpro.dto.v1.ProductDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.Product;
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
        Optional<Product> isProductExisting = IProductRepository.findProductByNameAndPrice(productDto.getName(), productDto.getPrice());
        if (isProductExisting.isPresent()) {
            return duplicityCheck(isProductExisting.get(), productDto) ?
                    MappingService.convertToDto(isProductExisting.get()) :
                    MappingService.convertToDto(IProductRepository.save(isProductExisting.get()));
        }

        Product product = new Product(productDto.getName(), productDto.getQuantity(), productDto.getPrice(),
                productDto.getTax(), productDto.getTotal());
        return MappingService.convertToDto(IProductRepository.save(product));
    }

    public ProductDto updateProduct(ProductDto productDto) {
        Product product = IProductRepository.findById(productDto.getId())
                .orElseThrow(() -> new RuntimeException(String.format("Product with id %s doesn't exist, can't be updated",
                        productDto.getId())));

        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setTaxPrice(productDto.getTax());
        product.setTotalPrice(productDto.getTotal());
        product.setQuantity(productDto.getQuantity());

        return MappingService.convertToDto(IProductRepository.save(product));
    }

    public ProductDto deleteProduct(UUID id) {
        Product product = IProductRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Product with id %s doesn't exist, can't be deleted", id)));

        List<Invoice> listWhichContainsProduct = lInvoiceRepository.findAllByProductListContains(product);

        if (!listWhichContainsProduct.isEmpty()) {
            throw new RuntimeException(String.format("Product with id %s can't be deleted, is in invoices", id));
        }

        IProductRepository.delete(product);
        return MappingService.convertToDto(product);
    }

    public List<ProductDto> findAllByPrice(BigDecimal price) {
        return IProductRepository.findAllByPrice(price).stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<ProductDto> findAllByNameContaining(String name) {
        return IProductRepository.findAllByNameContaining(name).stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<Product> findProductsByIds(List<UUID> ids) {
        return IProductRepository.findByIdIn(ids);
    }

    public Optional<Product> findProductById(UUID id) {
        return IProductRepository.findById(id);
    }

    protected boolean duplicityCheck(Product product, ProductDto productDto) {
        boolean isNameMatching = product.getName().equals(productDto.getName());
        boolean isNamePrice = product.getPrice().equals(productDto.getPrice());
        return isNameMatching && isNamePrice;
    }
}
