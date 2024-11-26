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

    public ProductDto createProduct(ProductDto productDto) {
        validateProduct(productDto);
        Optional<Product> isProductExisting = productRepository.findProductByNameAndPrice(productDto.getName(), productDto.getPrice());
        if (isProductExisting.isPresent()) {
            return duplicityCheck(isProductExisting.get(), productDto) ?
                    MappingService.convertToDto(isProductExisting.get()) :
                    MappingService.convertToDto(productRepository.save(isProductExisting.get()));
        }

        Product product = new Product(productDto.getName(), productDto.getQuantity(), productDto.getPrice(),
                productDto.getTax(), productDto.getTotal());
        return MappingService.convertToDto(productRepository.save(product));
    }

    public ProductDto updateProduct(ProductDto productDto) {
        Product product = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new RuntimeException(String.format("Product with id %s doesn't exist, can't be updated",
                        productDto.getId())));

        validateProduct(productDto);

        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setTaxPrice(productDto.getTax());
        product.setTotalPrice(productDto.getTotal());
        product.setQuantity(productDto.getQuantity());

        return MappingService.convertToDto(productRepository.save(product));
    }

    public ProductDto deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Product with id %s doesn't exist, can't be deleted", id)));

        List<Invoice> listWhichContainsProduct = invoiceService.findAllContainsProduct(product);
        if (!listWhichContainsProduct.isEmpty()) {
            throw new RuntimeException(String.format("Product with id %s can't be deleted, is in invoices", id));
        }

        productRepository.delete(product);
        return MappingService.convertToDto(product);
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

    protected boolean duplicityCheck(Product product, ProductDto productDto) {
        boolean isNameMatching = product.getName().equals(productDto.getName());
        boolean isNamePrice = product.getPrice().equals(productDto.getPrice());
        return isNameMatching && isNamePrice;
    }


    protected void validateProduct(ProductDto productDto) {
        if (productDto.getName() == null) {
            throw new RuntimeException("Product name is null or empty");
        }
        if (productDto.getQuantity() == null || productDto.getQuantity() <= 0) {
            throw new RuntimeException("Product quantity is null or negative");
        }
        if (productDto.getPrice() == null || productDto.getPrice().compareTo(new BigDecimal(0)) <= 0) {
            throw new RuntimeException("Product price is null or negative");
        }
        if (productDto.getTax() == null || productDto.getTax().compareTo(new BigDecimal(0)) <= 0) {
            throw new RuntimeException("Product price is null or negative");
        }
        if (productDto.getTax().add(productDto.getPrice()).equals(productDto.getTotal())) {
            throw new RuntimeException("Raw price plus tax doens't match total price!");
        }
    }
}
