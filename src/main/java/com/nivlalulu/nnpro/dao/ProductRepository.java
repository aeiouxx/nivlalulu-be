package com.nivlalulu.nnpro.dao;

import com.nivlalulu.nnpro.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<InvoiceItem, UUID> {

    Optional<InvoiceItem> findById(UUID id);

    List<InvoiceItem> findAllByPrice(BigDecimal price);

    List<InvoiceItem> findAllByNameContaining(String name);

    List<InvoiceItem> findByIdIn(List<UUID> productLists);

    boolean existsByNameAndPrice(String name, BigDecimal price);

    Optional<InvoiceItem> findProductByNameAndPrice(String name, BigDecimal price);

}