package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dao.ProductRepository;
import com.nivlalulu.nnpro.dto.ProductDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.Product;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private MappingService mappingService;

    public ProductDto createProduct(ProductDto productDto) {
        boolean isProductExisting = productRepository.existsByNameAndPriceAndVisible(productDto.getName(),
                productDto.getPrice(), true);
        if (isProductExisting) {
            throw new RuntimeException(String.format("Product with name %s and price %s exists", productDto.getName(), productDto.getPrice()));
        }
        Product product = new Product(productDto.getName(), productDto.getQuantity(), productDto.getPrice());
        return mappingService.convertToDto(productRepository.save(product));
    }

    public ProductDto updateProduct(ProductDto productDto) {
        Product product = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new RuntimeException(String.format("Product with id %s doesn't exist, can't be updated",
                        productDto.getId())));

        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());

        return mappingService.convertToDto(productRepository.save(product));
    }

    public ProductDto deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Product with id %s doesn't exist, can't be deleted", id)));

        List<Invoice> listWhichContainsProduct = invoiceService.findAllContainsProduct(product);
        if (!listWhichContainsProduct.isEmpty()) {
            throw new RuntimeException(String.format("Product with id %s can't be deleted, is in invoices", id));
        }

        productRepository.delete(product);
        return mappingService.convertToDto(product);
    }

    public List<ProductDto> findAllByPrice(BigDecimal price) {
        return productRepository.findAllByPrice(price).stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<ProductDto> findAllByNameContaining(String name) {
        return productRepository.findAllByNameContaining(name).stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<Product> findProductsByIds(List<UUID> ids) {
        return productRepository.findByIdIn(ids);
    }

    public Optional<Product> findProductById(UUID id) {
        return productRepository.findById(id);
    }

    public void validateProduct(ProductDto productDto) {
        if (productDto.getName() == null) {
            throw new RuntimeException("Product name is null or empty");
        }
        if (productDto.getQuantity() == null || productDto.getQuantity() <= 0) {
            throw new RuntimeException("Product quantity is null or negative");
        }
        if (productDto.getPrice() == null || productDto.getPrice().compareTo(new BigDecimal(0)) <= 0) {
            throw new RuntimeException("Product price is null or negative");
        }

    }
}
