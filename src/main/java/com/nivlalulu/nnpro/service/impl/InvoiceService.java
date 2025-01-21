package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.exceptions.NotFoundException;
import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.InvoiceSearchDto;
import com.nivlalulu.nnpro.dto.v1.PartySnapshotDto;
import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.model.Party;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IInvoiceRepository;
import com.nivlalulu.nnpro.repository.IPartyRepository;
import com.nivlalulu.nnpro.security.ownership.IsOwnedByUser;
import com.nivlalulu.nnpro.service.IInvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceService implements IInvoiceService {
    private final IInvoiceRepository invoiceRepository;
    private final IPartyRepository partyRepository;
    private final GenericModelMapper mapper;


    @Override

    public Page<InvoiceDto> search(User user, InvoiceSearchDto criteria, Pageable pageable, Sort sort) {
        Specification<Invoice> specification = criteria.toSpecification(user);

        if (pageable == null) {
            pageable = sort == null
                    ? Pageable.unpaged()
                    : Pageable.unpaged(sort);
        } else if (sort != null) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }

        return invoiceRepository.findAll(specification, pageable).map(mapper::convertToDto);
    }

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
    @IsOwnedByUser(entityClass = Invoice.class)
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
        var invoice = new Invoice(
                invoiceDto.getCreatedAt(),
                invoiceDto.getExpiresAt(),
                invoiceDto.getPaymentMethod(),
                invoiceDto.getVariableSymbol(),
                items,
                invoiceDto.getContact(),
                user
        );
        // set customer and supplier references + snapshots
        handlePartyUpdate(invoiceDto.getSupplier(), invoice::setSupplier, invoice::snapshotSupplier, user);
        handlePartyUpdate(invoiceDto.getCustomer(), invoice::setCustomer, invoice::snapshotCustomer, user);
        items.forEach(item -> item.setInvoice(invoice));
        var created = invoiceRepository.save(invoice);
        var dto = mapper.convertToDto(created);
        return dto;
    }

    @Override
    @IsOwnedByUser(entityClass = Invoice.class)
    public InvoiceDto updateInvoice(UUID id,
                                    InvoiceDto invoiceUpdated,
                                    User user) {
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

        handlePartyUpdate(invoiceUpdated.getSupplier(), invoice::setSupplier, invoice::snapshotSupplier, user);
        handlePartyUpdate(invoiceUpdated.getCustomer(), invoice::setCustomer, invoice::snapshotCustomer, user);

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
    @IsOwnedByUser(entityClass = Invoice.class)
    public InvoiceDto deleteInvoice(UUID id) {
        var invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Invoice", "id", id.toString()));
        invoiceRepository.delete(invoice);
        return mapper.convertToDto(invoice);
    }

    private Party getCreateParty(PartySnapshotDto party, User user) {
        Optional<Party> found = Optional.empty();
        if (party.getIcTax() != null) {
            found = partyRepository.findByIcTaxAndUser(party.getIcTax(), user);
        }
        else if (party.getDicTax() != null) {
            found = partyRepository.findByDicTaxAndUser(party.getDicTax(), user);
        }
        return found.orElseGet(() -> {
            var entity = mapper.snapshotToEntity(party);
            entity.setUser(user);
            return partyRepository.save(entity);
        });
    }

    // If we're editing a historical invoice, the state of the update contact information does not
    // have to reflect the state of the party in the party table, we might be migrating
    // from one snapshot to another, but both might be out of date with the current state of the party.
    // We don't validate on purpose to preserve the archival nature of the invoice.
    private void handlePartyUpdate(PartySnapshotDto partyDto,
                                   Consumer<Party> setReference,
                                   Consumer<Party> takeSnapshot,
                                   User user) {
        if (partyDto == null) {
            return;
        }

        // set the reference to the party
        Party reference = getCreateParty(partyDto, user);
        setReference.accept(reference);

        // but persist information supplied by the DTO instead of up to date information we retrieved in the
        // reference
        Party party = mapper.snapshotToEntity(partyDto);
        party.setUser(user);
        takeSnapshot.accept(party);
    }
}
