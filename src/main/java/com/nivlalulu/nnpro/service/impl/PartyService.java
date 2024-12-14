package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.dto.PartyDto;
import com.nivlalulu.nnpro.model.Party;
import com.nivlalulu.nnpro.repository.IPartyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartyService {

    private final IPartyRepository IPartyRepository;

    public PartyDto createParty(PartyDto partyDto) {
        Optional<Party> existingParty = IPartyRepository.findByTaxIdOrCompanyId(partyDto.getTaxId(), partyDto.getCompanyId());
        if (existingParty.isPresent()) {
            return duplicityCheck(partyDto, existingParty.get()) ?
                    MappingService.convertToDto(existingParty.get()) :
                    MappingService.convertToDto(IPartyRepository.save(existingParty.get()));
        }

        Party party = new Party(partyDto.getOrganizationName(), partyDto.getPersonName(), partyDto.getAddress(),
                partyDto.getCountry(), partyDto.getCompanyId(), partyDto.getTaxId(), partyDto.getTelephone(), partyDto.getEmail());
        return MappingService.convertToDto(IPartyRepository.save(party));
    }

    public PartyDto updateParty(PartyDto partyDto) {
        Party party = IPartyRepository.findById(partyDto.getId())
                .orElseThrow(() -> new RuntimeException(
                        String.format("Party with id %s doesn't exist, can't be updated", partyDto.getId())));

        party.setOrganizationName(partyDto.getOrganizationName());
        party.setPersonName(partyDto.getPersonName());
        party.setAddress(partyDto.getAddress());
        party.setCountry(partyDto.getCountry());
        party.setCompanyId(partyDto.getCompanyId());
        party.setTaxId(partyDto.getTaxId());
        party.setTelephone(partyDto.getTelephone());
        party.setEmail(partyDto.getEmail());

        //TODO atdy možná může být problem tou vazbou v Party entita
        return MappingService.convertToDto(IPartyRepository.save(party));
    }

    public PartyDto deleteParty(UUID id) {
        Party party = IPartyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("Party with id %s doesn't exist, can't be updated", id)));

        if (!party.getCustomerInvoices().isEmpty()) {
            throw new RuntimeException("Can't delete party because is in some Invoice as customer!");
        }
        if (!party.getSupplierInvoices().isEmpty()) {
            throw new RuntimeException("Can't delete party because is in some Invoice as supplier!");
        }

        IPartyRepository.delete(party);
        return MappingService.convertToDto(party);
    }

    public Optional<PartyDto> findById(UUID id) {
        Optional<Party> party = IPartyRepository.findById(id);
        return party.isPresent() ? Optional.of(MappingService.convertToDto(party.get())) : null;
    }

    public Optional<PartyDto> findByTaxId(String taxId) {
        Optional<Party> party = IPartyRepository.findByTaxId(taxId);
        return party.isPresent() ? Optional.of(MappingService.convertToDto(party.get())) : null;
    }

    public Optional<PartyDto> findByCompanyId(String companyId) {
        Optional<Party> party = IPartyRepository.findByCompanyId(companyId);
        return party.isPresent() ? Optional.of(MappingService.convertToDto(party.get())) : null;
    }

    private boolean duplicityCheck(PartyDto partyDto, Party party) {
        boolean isTaxIdMatching = partyDto.getTaxId().equals(party.getTaxId());
        boolean isCompanyIdMatching = partyDto.getCompanyId().equals(party.getCompanyId());
        return isTaxIdMatching && isCompanyIdMatching;
    }


}