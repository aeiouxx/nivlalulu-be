package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.exceptions.NotFoundException;
import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.repository.IInvoiceRepository;
import com.nivlalulu.nnpro.repository.IInvoiceItemRepository;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.service.IInvoiceItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceItemService implements IInvoiceItemService {

    private final IInvoiceItemRepository invoiceItemRepository;
    private final IInvoiceRepository invoiceRepository;
    private final GenericModelMapper mapper;

    @Override
    public Page<InvoiceItemDto> findForInvoice(Invoice invoice, Pageable pageable) {
        return invoiceItemRepository.findByInvoice(invoice, pageable).map(mapper::convertToDto);
    }

    @Override
    public List<InvoiceItemDto> findForInvoice(Invoice invoice) {
        return invoiceItemRepository.findByInvoice(invoice)
                .stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceItemDto findInvoiceItemByIdForInvoice(UUID id, Invoice invoice) {
        Optional<InvoiceItem> invoiceItem = invoiceItemRepository.findByIdAndInvoice(id, invoice);
        if (invoiceItem.isEmpty()) {
            throw new NotFoundException("Invoice item", "id", id.toString());
        }
        return mapper.convertToDto(invoiceItem.get());
    }

    @Override
    public InvoiceItemDto createInvoiceItem(InvoiceItemDto invoiceItemDto) {
//        var invoice = invoiceRepository.findById(invoiceItemDto.getInvoice().getId())
//                .orElseThrow(() -> new NotFoundException("Invoice", "id", invoiceItemDto.getInvoice().getId().toString()));
//        var invoiceItem = new InvoiceItem(
//                invoiceItemDto.getName(),
//                invoiceItemDto.getQuantity(),
//                invoiceItemDto.getUnitPrice(),
//                invoiceItemDto.getTaxPrice(),
//                invoiceItemDto.getTotalPrice(),
//                invoice
//        );
//
//        var created = invoiceItemRepository.save(invoiceItem);
//        return mapper.convertToDto(created);
        throw new NotImplementedException("There is no point to this as a direct call from a controller, imo," +
                "could be called by InvoiceService to handle stuff, but we don't need dtos for that");
    }

    @Override
    public InvoiceItemDto updateInvoiceItem(InvoiceItemDto invoiceItemUpdated) {
//        var invoiceItem = invoiceItemRepository.findByIdAndInvoice(invoiceItemUpdated.getId(),
//                        mapper.convertToEntity(invoiceItemUpdated.getInvoice()))
//                .orElseThrow(() -> new NotFoundException("Invoice item", "id", invoiceItemUpdated.getId().toString()));
//
//        if (invoiceItemUpdated.getName() != null) {
//            invoiceItem.setName(invoiceItemUpdated.getName());
//        }
//
//        if (invoiceItemUpdated.getUnitPrice() != null) {
//            invoiceItem.setUnitPrice(invoiceItemUpdated.getUnitPrice());
//        }
//
//        if (invoiceItemUpdated.getTaxPrice() != null) {
//            invoiceItem.setTaxPrice(invoiceItemUpdated.getTaxPrice());
//        }
//
//        if (invoiceItemUpdated.getTotalPrice() != null) {
//            invoiceItem.setTotalPrice(invoiceItemUpdated.getTotalPrice());
//        }
//
//        if (invoiceItemUpdated.getQuantity() != null) {
//            invoiceItem.setQuantity(invoiceItemUpdated.getQuantity());
//        }
//
//        var updated = invoiceItemRepository.save(invoiceItem);
//        return mapper.convertToDto(updated);
        throw new NotImplementedException("There is no point to this as a direct call from a controller, imo," +
                "could be called by InvoiceService to handle stuff, but we don't need dtos for that");
    }

    @Override
    public InvoiceItemDto deleteInvoiceItem(UUID id) {
//        var invoiceItem = invoiceItemRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("Invoice item", "id", id.toString()));
//        invoiceItemRepository.delete(invoiceItem);
//        return mapper.convertToDto(invoiceItem);

        throw new NotImplementedException("There is no point to this as a direct call from a controller, imo," +
                "could be called by InvoiceService to handle stuff, but we don't need dtos for that");
    }

    public List<InvoiceItemDto> findAllByPrice(BigDecimal price) {
        return invoiceItemRepository.findAllByUnitPrice(price).stream().map(mapper::convertToDto).collect(Collectors.toList());
    }

}
