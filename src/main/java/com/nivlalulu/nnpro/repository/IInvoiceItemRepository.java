package com.nivlalulu.nnpro.repository;

import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.InvoiceItem;
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

    List<InvoiceItem> findAllByNameContaining(String name);

    List<InvoiceItem> findByIdIn(List<UUID> productLists);

    Optional<InvoiceItem> findProductByNameAndUnitPrice(String name, BigDecimal price);
}