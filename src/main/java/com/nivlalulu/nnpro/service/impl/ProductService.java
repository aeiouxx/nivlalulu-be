package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.repository.lInvoiceRepository;
import com.nivlalulu.nnpro.repository.IProductRepository;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
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

    public InvoiceItemDto createProduct(InvoiceItemDto invoiceItemDto) {
        Optional<InvoiceItem> isProductExisting = IProductRepository.findProductByNameAndUnitPrice(invoiceItemDto.getName(), invoiceItemDto.getPrice());
        if (isProductExisting.isPresent()) {
            return duplicityCheck(isProductExisting.get(), invoiceItemDto) ?
                    MappingService.convertToDto(isProductExisting.get()) :
                    MappingService.convertToDto(IProductRepository.save(isProductExisting.get()));
        }

        InvoiceItem invoiceItem = new InvoiceItem(invoiceItemDto.getName(), invoiceItemDto.getQuantity(), invoiceItemDto.getPrice(),
                invoiceItemDto.getTax(), invoiceItemDto.getTotal());
        return MappingService.convertToDto(IProductRepository.save(invoiceItem));
    }

    public InvoiceItemDto updateProduct(InvoiceItemDto invoiceItemDto) {
        InvoiceItem invoiceItem = IProductRepository.findById(invoiceItemDto.getId())
                .orElseThrow(() -> new RuntimeException(String.format("Product with id %s doesn't exist, can't be updated",
                        invoiceItemDto.getId())));

        invoiceItem.setName(invoiceItemDto.getName());
        invoiceItem.setUnitPrice(invoiceItemDto.getPrice());
        invoiceItem.setTaxPrice(invoiceItemDto.getTax());
        invoiceItem.setTotalPrice(invoiceItemDto.getTotal());
        invoiceItem.setQuantity(invoiceItemDto.getQuantity());

        return MappingService.convertToDto(IProductRepository.save(invoiceItem));
    }

    public InvoiceItemDto deleteProduct(UUID id) {
        InvoiceItem invoiceItem = IProductRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Product with id %s doesn't exist, can't be deleted", id)));

        List<Invoice> listWhichContainsProduct = lInvoiceRepository.findAllByProductListContains(invoiceItem);

        if (!listWhichContainsProduct.isEmpty()) {
            throw new RuntimeException(String.format("Product with id %s can't be deleted, is in invoices", id));
        }

        IProductRepository.delete(invoiceItem);
        return MappingService.convertToDto(invoiceItem);
    }

    public List<InvoiceItemDto> findAllByPrice(BigDecimal price) {
        return IProductRepository.findAllByUnitPrice(price).stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<InvoiceItemDto> findAllByNameContaining(String name) {
        return IProductRepository.findAllByNameContaining(name).stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<InvoiceItem> findProductsByIds(List<UUID> ids) {
        return IProductRepository.findByIdIn(ids);
    }

    public Optional<InvoiceItem> findProductById(UUID id) {
        return IProductRepository.findById(id);
    }

    protected boolean duplicityCheck(InvoiceItem invoiceItem, InvoiceItemDto invoiceItemDto) {
        boolean isNameMatching = invoiceItem.getName().equals(invoiceItemDto.getName());
        boolean isNamePrice = invoiceItem.getUnitPrice().equals(invoiceItemDto.getPrice());
        return isNameMatching && isNamePrice;
    }
}
