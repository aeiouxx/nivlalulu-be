package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dao.ProductRepository;
import com.nivlalulu.nnpro.dto.InvoiceDto;
import com.nivlalulu.nnpro.dto.ProductDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.Product;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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
        Optional<Invoice> invoice = invoiceService.findInvoiceById(productDto.getInvoiceId());
        product.setInvoice(invoice.orElse(null));

        return mappingService.convertToDto(productRepository.save(product));
    }

    public ProductDto deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Product with id %s doesn't exist, can't be updated", id)));

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

}
