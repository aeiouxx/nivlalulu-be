package com.nivlalulu.nnpro.repository;

import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface lInvoiceRepository extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findById(UUID id);

    List<Invoice> findAllByProductListContains(InvoiceItem invoiceItem);

}