package com.nivlalulu.nnpro.repository;

import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(value = Invoice.WITH_ITEMS_GRAPH, type = EntityGraph.EntityGraphType.FETCH)
    Optional<InvoiceItem> findByIdAndInvoice(UUID id, Invoice invoice);
}