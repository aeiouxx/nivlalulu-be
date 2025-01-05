package com.nivlalulu.nnpro.repository;

import com.nivlalulu.nnpro.model.Party;
import com.nivlalulu.nnpro.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IPartyRepository extends JpaRepository<Party, UUID> {
    Optional<Party> findById(UUID id);

    Optional<Party> findByTaxId(String taxId);

    Page<Party> findByUser(User user, Pageable pageable);

    boolean existsByIdAndUser(UUID id, User user);

    Optional<Party> findByIcTaxAndUser(String icTax, User user);
    Optional<Party> findByDicTaxAndUser(String dicTax, User user);
    Optional<Party> findByIdAndUser(UUID id, User user);

}
