package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.exceptions.NotFoundException;
import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import com.nivlalulu.nnpro.dto.v1.UserDto;
import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IInvoiceRepository;
import com.nivlalulu.nnpro.repository.IUserRepository;
import com.nivlalulu.nnpro.repository.IInvoiceItemRepository;
import com.nivlalulu.nnpro.dto.v1.InvoiceItemDto;
import com.nivlalulu.nnpro.model.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceItemService {

    private final IInvoiceItemRepository IInvoiceItemRepository;
    private final IInvoiceRepository IInvoiceRepository;

    private final IUserRepository IUserRepository;

    private final UserService userService;

    private final GenericModelMapper mapper;

    public InvoiceItemDto createInvoiceItem(InvoiceItemDto invoiceItemDto, UserDto userDto) {
        if (isUserIdMatching(invoiceItemDto.getUserId(), userDto.getId())) {
            throw new RuntimeException("User isn't linked to invoice item");
        }
        Optional<InvoiceItem> isProductExisting = IInvoiceItemRepository.findProductByNameAndUnitPrice(invoiceItemDto.getName(), invoiceItemDto.getUnitPrice());
        if (isProductExisting.isPresent()) {
            if (!duplicityCheck(isProductExisting.get(), invoiceItemDto)) {
                User user = userService.findUserById(invoiceItemDto.getUserId());
                user.getInvoiceItems().add(isProductExisting.get());
                IUserRepository.save(user);
                mapper.convertToDto(IInvoiceItemRepository.save(isProductExisting.get()));
            } else {
                return mapper.convertToDto(isProductExisting.get());
            }
        }

        InvoiceItem invoiceItem = new InvoiceItem(invoiceItemDto.getName(), invoiceItemDto.getQuantity(), invoiceItemDto.getUnitPrice(),
                invoiceItemDto.getTaxPrice(), invoiceItemDto.getTotalPrice());
        return mapper.convertToDto(IInvoiceItemRepository.save(invoiceItem));
    }

    public InvoiceItemDto updateInvoiceItem(InvoiceItemDto invoiceItemDto, UserDto userDto) {
        InvoiceItem invoiceItem = checkIfInvoiceItemExisting(invoiceItemDto.getId(), userDto);

        invoiceItem.setName(invoiceItemDto.getName());
        invoiceItem.setUnitPrice(invoiceItemDto.getUnitPrice());
        invoiceItem.setTaxPrice(invoiceItemDto.getTaxPrice());
        invoiceItem.setTotalPrice(invoiceItemDto.getTotalPrice());
        invoiceItem.setQuantity(invoiceItemDto.getQuantity());

        return mapper.convertToDto(IInvoiceItemRepository.save(invoiceItem));
    }

    public InvoiceItemDto deleteInvoiceItem(UUID id, UserDto userDto) {
        InvoiceItem invoiceItem = checkIfInvoiceItemExisting(id, userDto);

        List<Invoice> listWhichContainsProduct = IInvoiceRepository.findAllByInvoiceItemListContains(invoiceItem);

        if (!listWhichContainsProduct.isEmpty()) {
            throw new RuntimeException(String.format("Product with id %s can't be deleted, is in invoices", id));
        }

        User user = userService.findUserById(invoiceItem.getUser().getId());
        user.getInvoiceItems().remove(invoiceItem);
        IUserRepository.save(user);

        IInvoiceItemRepository.delete(invoiceItem);
        return mapper.convertToDto(invoiceItem);
    }

    public List<InvoiceItemDto> findAllByPrice(BigDecimal price) {
        return IInvoiceItemRepository.findAllByUnitPrice(price).stream().map(mapper::convertToDto).collect(Collectors.toList());
    }

    public List<InvoiceItemDto> findAllByNameContaining(String name) {
        return IInvoiceItemRepository.findAllByNameContaining(name).stream().map(mapper::convertToDto).collect(Collectors.toList());
    }

    public List<InvoiceItem> findProductsByIds(List<UUID> ids) {
        return IInvoiceItemRepository.findByIdIn(ids);
    }

    public InvoiceItem findProductById(UUID id, UserDto userDto) {
        return checkIfInvoiceItemExisting(id, userDto);
    }

    public List<InvoiceItemDto> findAllInvoiceItems() {
        return IInvoiceItemRepository.findAll().stream().map(mapper::convertToDto).collect(Collectors.toList());
    }

    protected boolean duplicityCheck(InvoiceItem invoiceItem, InvoiceItemDto invoiceItemDto) {
        boolean isNameMatching = invoiceItem.getName().equals(invoiceItemDto.getName());
        boolean isNamePrice = invoiceItem.getUnitPrice().equals(invoiceItemDto.getUnitPrice());
        return isNameMatching && isNamePrice;
    }

    public InvoiceItem checkIfInvoiceItemExisting(UUID invoiceItemId, UserDto userDto) {
        Optional<InvoiceItem> existingInvoiceItem = IInvoiceItemRepository.findById(invoiceItemId);
        if (existingInvoiceItem.isEmpty()) {
            throw new NotFoundException("Invoice item", "id", invoiceItemId.toString());
        } else if (!isUserIdMatching(existingInvoiceItem.get().getUser().getId(), userDto.getId())) {
            throw new RuntimeException("User isn't linked to invoice item");
        }
        return existingInvoiceItem.get();
    }

    private boolean isUserIdMatching(Long id, Long userId) {
        return id.equals(userId);
    }
}
