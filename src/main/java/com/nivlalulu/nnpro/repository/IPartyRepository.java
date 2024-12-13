package com.nivlalulu.nnpro.repository;

import com.nivlalulu.nnpro.model.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IPartyRepository extends JpaRepository<Party, UUID> {

    Optional<Party> findById(UUID id);

    Optional<Party> findByTaxId(String taxId);

    Optional<Party> findByTaxIdOrCompanyId(String taxId, String companyId);

    Optional<Party> findByCompanyId(String companyId);

}
