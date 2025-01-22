package com.nivlalulu.nnpro.repository;

import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.InvoiceItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IInvoiceItemRepository extends JpaRepository<InvoiceItem, UUID> {
    Optional<InvoiceItem> findById(UUID id);

    List<InvoiceItem> findAllByUnitPrice(BigDecimal price);

    Page<InvoiceItem> findByInvoice(Invoice invoice, Pageable pageable);

    List<InvoiceItem> findByInvoice(Invoice invoice);

    Optional<InvoiceItem> findByIdAndInvoice(UUID id, Invoice invoice);
}