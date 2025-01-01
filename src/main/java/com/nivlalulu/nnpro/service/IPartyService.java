package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dto.v1.PartyDto;
import com.nivlalulu.nnpro.dto.v1.UserDto;
import com.nivlalulu.nnpro.model.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IPartyService {
    PartyDto createParty(PartyDto partyDto, UserDto userDto);

    PartyDto updateParty(PartyDto partyDto, UserDto userDto);

    PartyDto deleteParty(UUID id, UserDto userDto);

    PartyDto findById(UUID id, UserDto userDto);

    Optional<PartyDto> findByTaxId(String taxId, UserDto userDto);

    Optional<PartyDto> findByCompanyId(String companyId, UserDto userDto);

    List<PartyDto> findAllParties();

    List<PartyDto> findUserParties(UserDto userDto);

    Party checkIfInvoiceExisting(UUID partyId);
}
