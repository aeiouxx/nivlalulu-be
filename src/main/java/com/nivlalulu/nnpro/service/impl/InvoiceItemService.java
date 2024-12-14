package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.model.Party;
import com.nivlalulu.nnpro.repository.lInvoiceRepository;
import com.nivlalulu.nnpro.repository.IInvoiceItemRepository;
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
public class InvoiceItemService {

    private final IInvoiceItemRepository IInvoiceItemRepository;

    private final lInvoiceRepository lInvoiceRepository;

    public InvoiceItemDto createInvoiceItem(InvoiceItemDto invoiceItemDto) {
        Optional<InvoiceItem> isProductExisting = IInvoiceItemRepository.findProductByNameAndUnitPrice(invoiceItemDto.getName(), invoiceItemDto.getPrice());
        if (isProductExisting.isPresent()) {
            return duplicityCheck(isProductExisting.get(), invoiceItemDto) ?
                    MappingService.convertToDto(isProductExisting.get()) :
                    MappingService.convertToDto(IInvoiceItemRepository.save(isProductExisting.get()));
        }

        InvoiceItem invoiceItem = new InvoiceItem(invoiceItemDto.getName(), invoiceItemDto.getQuantity(), invoiceItemDto.getPrice(),
                invoiceItemDto.getTax(), invoiceItemDto.getTotal());
        return MappingService.convertToDto(IInvoiceItemRepository.save(invoiceItem));
    }

    public InvoiceItemDto updateInvoiceItem(InvoiceItemDto invoiceItemDto) {
        InvoiceItem invoiceItem = IInvoiceItemRepository.findById(invoiceItemDto.getId())
                .orElseThrow(() -> new RuntimeException(String.format("Product with id %s doesn't exist, can't be updated",
                        invoiceItemDto.getId())));

        invoiceItem.setName(invoiceItemDto.getName());
        invoiceItem.setUnitPrice(invoiceItemDto.getPrice());
        invoiceItem.setTaxPrice(invoiceItemDto.getTax());
        invoiceItem.setTotalPrice(invoiceItemDto.getTotal());
        invoiceItem.setQuantity(invoiceItemDto.getQuantity());

        return MappingService.convertToDto(IInvoiceItemRepository.save(invoiceItem));
    }

    public InvoiceItemDto deleteInvoiceItem(UUID id) {
        InvoiceItem invoiceItem = IInvoiceItemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Product with id %s doesn't exist, can't be deleted", id)));

        List<Invoice> listWhichContainsProduct = lInvoiceRepository.findAllByProductListContains(invoiceItem);

        if (!listWhichContainsProduct.isEmpty()) {
            throw new RuntimeException(String.format("Product with id %s can't be deleted, is in invoices", id));
        }

        IInvoiceItemRepository.delete(invoiceItem);
        return MappingService.convertToDto(invoiceItem);
    }

    public List<InvoiceItemDto> findAllByPrice(BigDecimal price) {
        return IInvoiceItemRepository.findAllByUnitPrice(price).stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<InvoiceItemDto> findAllByNameContaining(String name) {
        return IInvoiceItemRepository.findAllByNameContaining(name).stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    public List<InvoiceItem> findProductsByIds(List<UUID> ids) {
        return IInvoiceItemRepository.findByIdIn(ids);
    }

    public InvoiceItem findProductById(UUID id) {
        return checkIfInvoiceItemExisting(id);
    }

    public List<InvoiceItemDto> findAllInvoiceItems() {
        return IInvoiceItemRepository.findAll().stream().map(MappingService::convertToDto).collect(Collectors.toList());
    }

    protected boolean duplicityCheck(InvoiceItem invoiceItem, InvoiceItemDto invoiceItemDto) {
        boolean isNameMatching = invoiceItem.getName().equals(invoiceItemDto.getName());
        boolean isNamePrice = invoiceItem.getUnitPrice().equals(invoiceItemDto.getPrice());
        return isNameMatching && isNamePrice;
    }

    public InvoiceItem checkIfInvoiceItemExisting(UUID invoiceItemId) {
        Optional<InvoiceItem> existingInvoiceItem = IInvoiceItemRepository.findById(invoiceItemId);
        if (existingInvoiceItem.isEmpty()) {
            throw new RuntimeException(String.format("Invoice item with id %s doens't exists", invoiceItemId));
        } else {
            return existingInvoiceItem.get();
        }
    }
}