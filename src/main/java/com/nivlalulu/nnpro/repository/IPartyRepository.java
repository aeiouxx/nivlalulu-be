package com.nivlalulu.nnpro.repository;

import com.nivlalulu.nnpro.model.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IPartyRepository extends JpaRepository<Party, UUID> {

    Optional<Party> findById(UUID id);

    Optional<Party> findByTaxIdAndUserId(String taxId, Long userId);

    Optional<Party> findByTaxIdOrCompanyIdAndUserId(String taxId, String companyId, Long userId);

    Optional<Party> findByCompanyIdAndUserId(String companyId, Long userId);

    List<Party> findPartiesByUser(Long userId);

}
