package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.exceptions.NotFoundException;
import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import com.nivlalulu.nnpro.dto.v1.PartyDto;
import com.nivlalulu.nnpro.model.Party;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IPartyRepository;
import com.nivlalulu.nnpro.service.IPartyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyService implements IPartyService {
    private final IPartyRepository partyRepository;
    private final GenericModelMapper mapper;

    @Override
    public PartyDto createParty(PartyDto partyDto, User user) {
        var party = mapper.convertToEntity(partyDto);
        party.setUser(user);
        var created = partyRepository.save(party);
        return mapper.convertToDto(created);
    }

    @Override
    public PartyDto updateParty(PartyDto partyUpdated, User user) {
        var party = partyRepository.findByIdAndUser(partyUpdated.getId(), user)
                .orElseThrow(() -> new NotFoundException("Party", "id", partyUpdated.getId().toString()));

        if (partyUpdated.getName() != null) {
            party.setName(partyUpdated.getName());
        }
        if (partyUpdated.getAddress() != null) {
            party.setAddress(partyUpdated.getAddress());
        }
        if (partyUpdated.getCountry() != null) {
            party.setCountry(partyUpdated.getCountry());
        }
        if (partyUpdated.getIcTax() != null) {
            party.setIcTax(partyUpdated.getIcTax());
        }
        if (partyUpdated.getDicTax() != null) {
            party.setDicTax(partyUpdated.getDicTax());
        }
        if (partyUpdated.getTelephone() != null) {
            party.setTelephone(partyUpdated.getTelephone());
        }
        if (partyUpdated.getEmail() != null) {
            party.setEmail(partyUpdated.getEmail());
        }

        var updated = partyRepository.save(party);
        return mapper.convertToDto(updated);
    }

    @Override
    public PartyDto deleteParty(UUID id) {
        Party party = partyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Party", "id", id.toString()));
        partyRepository.delete(party);
        return mapper.convertToDto(party);
    }

    @Override
    public PartyDto findById(UUID id) {
        Party party = checkIfInvoiceExisting(id);
        return mapper.convertToDto(party);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PartyDto> findForUser(User user, Pageable pageable) {
        return partyRepository.findByUser(user, pageable).map(mapper::convertToDto);
    }

    private boolean duplicityCheck(PartyDto partyDto, Party party) {
        boolean isTaxIdMatching = partyDto.getDicTax().equals(party.getDicTax());
        boolean isCompanyIdMatching = partyDto.getIcTax().equals(party.getIcTax());
        return isTaxIdMatching && isCompanyIdMatching;
    }

    @Override
    public Party checkIfInvoiceExisting(UUID partyId) {
        Optional<Party> existingParty = partyRepository.findById(partyId);
        return partyRepository.findById(partyId)
                .orElseThrow(() -> {
                    log.error("Party with id {} doesn't exist.", partyId);
                    return new NotFoundException(Party.class);
                });
    }
}
