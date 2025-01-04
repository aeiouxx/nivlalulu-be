package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.exceptions.NotFoundException;
import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.repository.IInvoiceItemRepository;
import com.nivlalulu.nnpro.service.IInvoiceItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InvoiceItemService implements IInvoiceItemService {
    private final IInvoiceItemRepository invoiceItemRepository;
    private final GenericModelMapper mapper;

    @Override
    public Page<InvoiceItemDto> findForInvoice(Invoice invoice, Pageable pageable) {
        return invoiceItemRepository.findByInvoice(invoice, pageable)
                .map(mapper::convertToDto);
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
        return invoiceItemRepository.findByIdAndInvoice(id, invoice)
                .map(mapper::convertToDto)
                .orElseThrow(() -> new NotFoundException("InvoiceItem", "id", id.toString()));
    }
}
