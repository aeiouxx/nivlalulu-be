package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import com.nivlalulu.nnpro.dto.v1.UserDto;
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
    public InvoiceDto createInvoice(InvoiceDto invoiceDto, UserDto userDto) {
        if (!isUserIdMatching(invoiceDto.getUserId(), userDto.getId())) {
            throw new RuntimeException("User isn't linked to invoice");
        }
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
    public InvoiceDto updateInvoice(InvoiceDto invoiceUpdated, UserDto userDto) {
        Invoice invoice = checkIfInvoiceExisting(invoiceUpdated.getId(), userDto);

        invoice.setInvoiceItemList(invoiceUpdated.getProducts().stream().map(mapper::convertToEntity).collect(Collectors.toSet()));
        invoice.setExpiresAt(invoiceUpdated.getDueDate());

        return mapper.convertToDto(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceDto deleteInvoice(UUID id, UserDto userDto) {
        Invoice invoice = checkIfInvoiceExisting(id, userDto);
        User user = userService.findUserById(invoice.getUser().getId());
        user.getInvoices().remove(invoice);
        IUserRepository.save(user);
        invoiceRepository.delete(invoice);
        return mapper.convertToDto(invoice);
    }

    @Override
    public InvoiceDto addInvoiceItemToInvoice(UUID invoiceId, List<InvoiceItemDto> productsIds, UserDto userDto) {
        Invoice existingInvoice = checkIfInvoiceExisting(invoiceId, userDto);
        existingInvoice.getInvoiceItemList().addAll(validateInvoiceItemForInvoice(productsIds));
        return updateInvoice(mapper.convertToDto(existingInvoice), userDto);
    }

    public InvoiceDto removeInvoiceItemFromInvoice(UUID invoiceId, List<InvoiceItemDto> productsIds, UserDto userDto) {
        Invoice existingInvoice = checkIfInvoiceExisting(invoiceId, userDto);
        validateInvoiceItemForInvoice(productsIds).forEach(existingInvoice.getInvoiceItemList()::remove);
        return updateInvoice(mapper.convertToDto(existingInvoice), userDto);
    }


    @Override
    public InvoiceDto findInvoiceDtoById(UUID id, UserDto userDto) {
        Invoice invoice = checkIfInvoiceExisting(id, userDto);
        return mapper.convertToDto(invoice);
    }

    @Override
    public Optional<Invoice> findInvoiceById(UUID id, UserDto userDto) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (invoice.isEmpty()) {
            return Optional.empty();
        }
        if (!isUserIdMatching(invoice.get().getUser().getId(), userDto.getId())) {
            throw new RuntimeException("User isn't linked to invoice");
        }
        return invoice;
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

    public Invoice checkIfInvoiceExisting(UUID invoiceId, UserDto userDto) {
        Optional<Invoice> existingInvoice = findInvoiceById(invoiceId, userDto);
        if (existingInvoice.isEmpty()) {
            throw new NotFoundException("Invoice", "id", invoiceId.toString());
        } else if (!isUserIdMatching(existingInvoice.get().getUser().getId(), userDto.getId())) {
            throw new RuntimeException("User isn't linked to invoice");
        }
        return existingInvoice.get();
    }

    private boolean isUserIdMatching(Long id, Long userId) {
        return id.equals(userId);
    }

}
