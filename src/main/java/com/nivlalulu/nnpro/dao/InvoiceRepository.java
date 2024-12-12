package com.nivlalulu.nnpro.dao;

import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    Optional<Invoice> findById(UUID id);

    List<Invoice> findAllByProductListContains(Product product);

}