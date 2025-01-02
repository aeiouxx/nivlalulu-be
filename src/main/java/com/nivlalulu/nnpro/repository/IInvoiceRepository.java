package com.nivlalulu.nnpro.repository;

import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.InvoiceItem;
import com.nivlalulu.nnpro.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IInvoiceRepository extends JpaRepository<Invoice, UUID> {
    Page<Invoice> findByUser(User user, Pageable pageable);
    List<Invoice> findByUser(User user);
    boolean existsByIdAndUser(UUID id, User user);

    @EntityGraph(value = Invoice.WITH_ITEMS_GRAPH, type = EntityGraph.EntityGraphType.FETCH)
    Optional<Invoice> findByIdAndUser(UUID id, User user);
}