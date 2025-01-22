package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dto.v1.PartyDto;
import com.nivlalulu.nnpro.model.Party;
import com.nivlalulu.nnpro.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IPartyService {
    PartyDto createParty(PartyDto partyDto, User user);

    PartyDto updateParty(PartyDto partyDto, User user);

    PartyDto deleteParty(UUID id);

    PartyDto findById(UUID id);

    Page<PartyDto> findForUser(User user, Pageable pageable);

    Party checkIfInvoiceExisting(UUID partyId);
}
