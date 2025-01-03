package com.nivlalulu.nnpro.security.ownership.checkers;

import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IInvoiceRepository;
import com.nivlalulu.nnpro.security.ownership.IOwnershipChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InvoiceOwnershipChecker implements IOwnershipChecker<UUID> {
    private final IInvoiceRepository invoiceRepository;

    @Override
    public Class<?> getEntityClass() {
        return Invoice.class;
    }

    @Override
    public boolean resourceExists(UUID resourceId) {
        return invoiceRepository.existsById(resourceId);
    }

    @Override
    public boolean isOwnedBy(UUID resourceId, User user) {
        return invoiceRepository.existsByIdAndUser(resourceId, user);
    }
}
