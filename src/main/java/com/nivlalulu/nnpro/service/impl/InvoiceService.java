package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.exceptions.NotFoundException;
import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.dto.v1.PartyDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.model.Party;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IInvoiceItemRepository;
import com.nivlalulu.nnpro.repository.IInvoiceRepository;
import com.nivlalulu.nnpro.repository.IPartyRepository;
import com.nivlalulu.nnpro.service.IInvoiceItemService;
import com.nivlalulu.nnpro.service.IInvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceService implements IInvoiceService, IInvoiceItemService {
    private final ModelMapper modelMapper;
    private final IInvoiceRepository invoiceRepository;
    private final IInvoiceItemRepository invoiceItemRepository;
    private final IPartyRepository partyRepository;
    private final GenericModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDto> findForUser(User user, Pageable pageable) {
        return invoiceRepository.findByUser(user, pageable).map(mapper::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> findForUser(User user) {
        return invoiceRepository.findByUser(user)
                .stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDto findInvoiceByIdForUser(UUID id, User user) {
        return invoiceRepository.findByIdAndUser(id, user)
                .map(mapper::convertToDto)
                .orElseThrow(() -> new NotFoundException("Invoice", "id", id.toString()));
    }

    @Override
    public InvoiceDto createInvoice(InvoiceDto invoiceDto, User user) {
        var items = invoiceDto.getItems()
                .stream()
                .map(mapper::convertToEntity)
                .collect(Collectors.toSet());
        var customer = getCreateParty(invoiceDto.getCustomer(), user);
        var supplier = getCreateParty(invoiceDto.getSupplier(), user);
        var invoice = new Invoice(
                invoiceDto.getCreatedAt(),
                invoiceDto.getExpiresAt(),
                invoiceDto.getPaymentMethod(),
                invoiceDto.getVariableSymbol(),
                items,
                customer,
                supplier,
                invoiceDto.getContact(),
                user
        );
        items.forEach(item -> item.setInvoice(invoice));
        var created = invoiceRepository.save(invoice);
        var dto = mapper.convertToDto(created);
        return dto;
    }

    @Override
    public InvoiceDto updateInvoice(InvoiceDto invoiceUpdated, User user) {
        // cleanest code of all time Q_Q
        var invoice = invoiceRepository.findByIdAndUser(invoiceUpdated.getId(), user)
                .orElseThrow(() -> new NotFoundException("Invoice", "id", invoiceUpdated.getId().toString()));
        if (invoiceUpdated.getCreatedAt() != null) {
            invoice.setCreatedAt(invoiceUpdated.getCreatedAt());
        }

        if (invoiceUpdated.getExpiresAt() != null) {
            invoice.setExpiresAt(invoiceUpdated.getExpiresAt());
        }

        if (invoiceUpdated.getPaymentMethod() != null) {
            invoice.setPaymentMethod(invoiceUpdated.getPaymentMethod());
        }

        if (invoiceUpdated.getVariableSymbol() != null) {
            invoice.setVariableSymbol(invoiceUpdated.getVariableSymbol());
        }

        if (invoiceUpdated.getContact() != null) {
            invoice.setContact(invoiceUpdated.getContact());
        }

        if (invoiceUpdated.getCustomer() != null) {
            var customer = getCreateParty(invoiceUpdated.getCustomer(), user);
            invoice.setCustomer(customer);
        }

        if (invoiceUpdated.getSupplier() != null) {
            var supplier = getCreateParty(invoiceUpdated.getSupplier(), user);
            invoice.setSupplier(supplier);
        }

        if (invoiceUpdated.getItems() != null && !invoiceUpdated.getItems().isEmpty()) {
            invoice.getItems().clear();
            Set<InvoiceItem> newItems = invoiceUpdated.getItems()
                    .stream()
                    .map(mapper::convertToEntity) // InvoiceItemDto -> InvoiceItem
                    .collect(Collectors.toSet());
            newItems.forEach(item -> item.setInvoice(invoice));
            invoice.getItems().addAll(newItems);
        }

        var updated = invoiceRepository.save(invoice);
        return mapper.convertToDto(updated);
    }

    @Override
    public InvoiceDto deleteInvoice(UUID id) {
        var invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Invoice", "id", id.toString()));
        invoiceRepository.delete(invoice);
        return mapper.convertToDto(invoice);
    }


    private Party getCreateParty(PartyDto party, User user) {
        return partyRepository.findByTaxIdOrCompanyId(party.getTaxId(), party.getCompanyId())
                .orElseGet(() -> {
                    var entity = mapper.convertToEntity(party);
                    entity.setUser(user);
                    return partyRepository.save(entity);
                });
    }

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
    public InvoiceItemDto createInvoiceItem(InvoiceItemDto invoiceItemDto, InvoiceDto invoiceDto) {
        var invoiceItem = new InvoiceItem(
                invoiceItemDto.getName(),
                invoiceItemDto.getQuantity(),
                invoiceItemDto.getUnitPrice(),
                invoiceItemDto.getTaxPrice(),
                invoiceItemDto.getTotalPrice(),
                modelMapper.map(invoiceDto, Invoice.class)
        );

        var created = invoiceItemRepository.save(invoiceItem);
        return mapper.convertToDto(created);
    }

    @Override
    public InvoiceItemDto deleteInvoiceItem(UUID id) {
        var invoiceItem = invoiceItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Invoice item", "id", id.toString()));
        invoiceItemRepository.delete(invoiceItem);
        return mapper.convertToDto(invoiceItem);
    }

    public List<InvoiceItemDto> findAllByPrice(BigDecimal price) {
        return invoiceItemRepository.findAllByUnitPrice(price).stream().map(mapper::convertToDto).collect(Collectors.toList());
    }
}
