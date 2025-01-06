package com.nivlalulu.nnpro.repository;

import com.nivlalulu.nnpro.model.Invoice;
import com.nivlalulu.nnpro.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IInvoiceRepository extends JpaRepository<Invoice, UUID>, JpaSpecificationExecutor<Invoice> {
    Page<Invoice> findByUser(User user, Pageable pageable);
    List<Invoice> findByUser(User user);
    boolean existsByIdAndUser(UUID id, User user);

    @EntityGraph(value = Invoice.WITH_ITEMS_AND_PARTIES_GRAPH, type = EntityGraph.EntityGraphType.FETCH)
    Optional<Invoice> findByIdAndUser(UUID id, User user);
}