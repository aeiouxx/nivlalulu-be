package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import com.nivlalulu.nnpro.repository.IInvoiceRepository;
import com.nivlalulu.nnpro.common.exceptions.NotFoundException;
import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.model.Party;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IUserRepository;
import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;

import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.service.IInvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService implements IInvoiceService {
    private final IInvoiceRepository invoiceRepository;
    private final InvoiceItemService invoiceItemService;
    private final GenericModelMapper mapper;
    private final IUserRepository IUserRepository;
    private final UserService userService;

    @Override
    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {
        Set<InvoiceItem> invoiceItemList = invoiceDto.getProducts().stream().map(mapper::convertToEntity).collect(Collectors.toSet());
        Party customer = mapper.convertToEntity(invoiceDto.getCustomer());
        Party supplier = mapper.convertToEntity(invoiceDto.getSupplier());
        User user = userService.findUserById(invoiceDto.getUserId());

        Invoice invoice = new Invoice(invoiceDto.getIssueDate(), invoiceDto.getDueDate(),
                invoiceDto.getPaymentMethod(), invoiceDto.getVariableSymbol(), invoiceItemList, customer, supplier);
        invoiceItemList.forEach(product -> invoiceItemService.createInvoiceItem(mapper.convertToDto(product)));
        user.getInvoices().add(invoice);
        IUserRepository.save(user);
        return mapper.convertToDto(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceDto updateInvoice(InvoiceDto invoiceUpdated) {
        Invoice invoice = checkIfInvoiceExisting(invoiceUpdated.getId());

        invoice.setInvoiceItemList(invoiceUpdated.getProducts().stream().map(mapper::convertToEntity).collect(Collectors.toSet()));
        invoice.setExpiresAt(invoiceUpdated.getDueDate());

        return mapper.convertToDto(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceDto deleteInvoice(UUID id) {
        Invoice invoice = checkIfInvoiceExisting(id);
        User user = userService.findUserById(invoice.getUser().getId());
        user.getInvoices().remove(invoice);
        IUserRepository.save(user);
        invoiceRepository.delete(invoice);
        return mapper.convertToDto(invoice);
    }

    @Override
    public InvoiceDto addInvoiceItemToInvoice(UUID invoiceId, List<InvoiceItemDto> productsIds) {
        Invoice existingInvoice = checkIfInvoiceExisting(invoiceId);
        existingInvoice.getInvoiceItemList().addAll(validateInvoiceItemForInvoice(productsIds));
        return updateInvoice(mapper.convertToDto(existingInvoice));
    }

    public InvoiceDto removeInvoiceItemFromInvoice(UUID invoiceId, List<InvoiceItemDto> productsIds) {
        Invoice existingInvoice = checkIfInvoiceExisting(invoiceId);
        validateInvoiceItemForInvoice(productsIds).forEach(existingInvoice.getInvoiceItemList()::remove);
        return updateInvoice(mapper.convertToDto(existingInvoice));
    }


    @Override
    public InvoiceDto findInvoiceDtoById(UUID id) {
        Invoice invoice = checkIfInvoiceExisting(id);
        return mapper.convertToDto(invoice);
    }

    @Override
    public Optional<Invoice> findInvoiceById(UUID id) {
        return invoiceRepository.findById(id);
    }

    @Override
    public List<InvoiceDto> findAllInvoices() {
        return invoiceRepository
                .findAll()
                .stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Invoice> findAllContainsInvoiceItem(InvoiceItem invoiceItem) {
        return invoiceRepository.findAllByInvoiceItemListContains(invoiceItem);
    }

    public List<InvoiceItem> validateInvoiceItemForInvoice(List<InvoiceItemDto> productsIds) {
        List<InvoiceItem> invoiceItems = new ArrayList<>();
        for (InvoiceItemDto productId : productsIds) {
            InvoiceItem existingProduct = invoiceItemService.findProductById(productId.getId());
            invoiceItems.add(existingProduct);
        }
        return invoiceItems;
    }

    public Invoice checkIfInvoiceExisting(UUID invoiceId) {
        Optional<Invoice> existingInvoice = findInvoiceById(invoiceId);
        if (existingInvoice.isEmpty()) {
            throw new NotFoundException("Invoice", "id", invoiceId.toString());
        } else {
            return existingInvoice.get();
        }
    }

}
