package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dto.v1.PartyDto;
import com.nivlalulu.nnpro.model.Party;
import com.nivlalulu.nnpro.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPartyService {
    PartyDto createParty(PartyDto partyDto, User user);

    PartyDto updateParty(PartyDto partyDto, User user);

    PartyDto deleteParty(UUID id);

    PartyDto findById(UUID id);

    Optional<PartyDto> findByTaxId(String taxId);

    Optional<PartyDto> findByCompanyId(String companyId);

    List<PartyDto> findAllParties();

    Party checkIfInvoiceExisting(UUID partyId);
}
