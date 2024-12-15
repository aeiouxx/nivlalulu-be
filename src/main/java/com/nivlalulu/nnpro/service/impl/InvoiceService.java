package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.exceptions.NotFoundException;
import com.nivlalulu.nnpro.dto.v1.UserDto;
import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.model.Party;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IUserRepository;
import com.nivlalulu.nnpro.repository.lInvoiceRepository;
import com.nivlalulu.nnpro.dto.v1.InvoiceDto;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;

import com.nivlalulu.nnpro.model.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final lInvoiceRepository lInvoiceRepository;

    private final InvoiceItemService invoiceItemService;

    private final IUserRepository IUserRepository;

    private final UserService userService;

    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {

        Set<InvoiceItem> invoiceItemList = invoiceDto.getProducts().stream().map(MappingService::convertToEntity).collect(Collectors.toSet());
        Party customer = MappingService.convertToEntity(invoiceDto.getCustomer());
        Party supplier = MappingService.convertToEntity(invoiceDto.getSupplier());
        User user = userService.findUserById(invoiceDto.getUserId());

        Invoice invoice = new Invoice(invoiceDto.getIssueDate(), invoiceDto.getDueDate(),
                invoiceDto.getPaymentMethod(), invoiceDto.getVariableSymbol(), invoiceItemList, customer, supplier);
        invoiceItemList.forEach(product -> invoiceItemService.createInvoiceItem(MappingService.convertToDto(product)));
        user.getInvoices().add(invoice);
        IUserRepository.save(user);
        return MappingService.convertToDto(lInvoiceRepository.save(invoice));
    }

    public InvoiceDto updateInvoice(InvoiceDto invoiceUpdated) {
        Invoice invoice = checkIfInvoiceExisting(invoiceUpdated.getId());

        invoice.setInvoiceItemList(invoiceUpdated.getProducts().stream().map(MappingService::convertToEntity).collect(Collectors.toSet()));
        invoice.setExpiration(invoiceUpdated.getDueDate());

        return MappingService.convertToDto(lInvoiceRepository.save(invoice));
    }

    public InvoiceDto deleteInvoice(UUID id) {
        Invoice invoice = checkIfInvoiceExisting(id);
        User user = userService.findUserById(invoice.getUser().getId());
        user.getInvoices().remove(invoice);
        IUserRepository.save(user);
        lInvoiceRepository.delete(invoice);
        return MappingService.convertToDto(invoice);
    }

    public InvoiceDto addProductToInvoice(UUID invoiceId, List<InvoiceItemDto> productsIds) {
        Invoice existingInvoice = checkIfInvoiceExisting(invoiceId);
        existingInvoice.getInvoiceItemList().addAll(validateInvoiceItemForInvoice(productsIds));
        return updateInvoice(MappingService.convertToDto(existingInvoice));
    }

    public InvoiceDto removeProductFromInvoice(UUID invoiceId, List<InvoiceItemDto> productsIds) {
        Invoice existingInvoice = checkIfInvoiceExisting(invoiceId);
        validateInvoiceItemForInvoice(productsIds).forEach(existingInvoice.getInvoiceItemList()::remove);
        return updateInvoice(MappingService.convertToDto(existingInvoice));
    }


    public InvoiceDto findInvoiceDtoById(UUID id) {
        Invoice invoice = checkIfInvoiceExisting(id);
        return MappingService.convertToDto(invoice);
    }

    protected Optional<Invoice> findInvoiceById(UUID id) {
        return lInvoiceRepository.findById(id);
    }

    public List<InvoiceDto> findAllInvoices() {
        return lInvoiceRepository.findAll().stream().map(MappingService::convertToDto).collect(Collectors.toList());
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
