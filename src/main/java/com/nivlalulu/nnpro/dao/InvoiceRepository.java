package com.nivlalulu.nnpro.dao;

import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    List<Invoice> findAllByCompanyName(String companyName);

    List<Invoice> findAllByCompanyId(String companyId);

    List<Invoice> findAllByTaxId(String companyId);

    List<Invoice> findAllByUser(UUID userId);

}