package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.exceptions.NotFoundException;
import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import com.nivlalulu.nnpro.dto.v1.PartyDto;
import com.nivlalulu.nnpro.model.Party;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IPartyRepository;
import com.nivlalulu.nnpro.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartyService {

    private final IPartyRepository IPartyRepository;

    private final IUserRepository IUserRepository;

    private final UserService userService;

    private final GenericModelMapper mapper;

    public PartyDto createParty(PartyDto partyDto) {
        Optional<Party> existingParty = IPartyRepository.findByTaxIdOrCompanyId(partyDto.getTaxId(), partyDto.getCompanyId());
        if (existingParty.isPresent()) {
            if (!duplicityCheck(partyDto, existingParty.get())) {
                User user = userService.findUserById(existingParty.get().getUser().getId());
                user.getParties().add(existingParty.get());
                IUserRepository.save(user);
                mapper.convertToDto(IPartyRepository.save(existingParty.get()));
            } else {
                return mapper.convertToDto(existingParty.get());
            }
        }

        Party party = new Party(partyDto.getOrganizationName(), partyDto.getPersonName(), partyDto.getAddress(),
                partyDto.getCountry(), partyDto.getCompanyId(), partyDto.getTaxId(), partyDto.getTelephone(), partyDto.getEmail());
        return mapper.convertToDto(IPartyRepository.save(party));
    }

    public PartyDto updateParty(PartyDto partyDto) {
        Party party = IPartyRepository.findById(partyDto.getId())
                .orElseThrow(() -> new NotFoundException("Party", "id", partyDto.getId().toString()));

        party.setOrganizationName(partyDto.getOrganizationName());
        party.setPersonName(partyDto.getPersonName());
        party.setAddress(partyDto.getAddress());
        party.setCountry(partyDto.getCountry());
        party.setCompanyId(partyDto.getCompanyId());
        party.setTaxId(partyDto.getTaxId());
        party.setTelephone(partyDto.getTelephone());
        party.setEmail(partyDto.getEmail());

        //TODO atdy možná může být problem tou vazbou v Party entita
        return mapper.convertToDto(IPartyRepository.save(party));
    }

    public PartyDto deleteParty(UUID id) {
        Party party = IPartyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Party", "id", id.toString()));

        if (!party.getCustomerInvoices().isEmpty()) {
            throw new RuntimeException("Can't delete party because is in some Invoice as customer!");
        }
        if (!party.getSupplierInvoices().isEmpty()) {
            throw new RuntimeException("Can't delete party because is in some Invoice as supplier!");
        }

        User user = userService.findUserById(party.getUser().getId());
        user.getInvoiceItems().remove(user);
        IUserRepository.save(user);

        IPartyRepository.delete(party);
        return mapper.convertToDto(party);
    }

    public PartyDto findById(UUID id) {
        Party party = checkIfInvoiceExisting(id);
        return mapper.convertToDto(party);
    }

    public Optional<PartyDto> findByTaxId(String taxId) {
        Optional<Party> party = IPartyRepository.findByTaxId(taxId);
        return party.map(value -> Optional.of(mapper.convertToDto(value))).orElse(null);
    }

    public Optional<PartyDto> findByCompanyId(String companyId) {
        Optional<Party> party = IPartyRepository.findByCompanyId(companyId);
        return party.map(value -> Optional.of(mapper.convertToDto(value))).orElse(null);
    }

    public List<PartyDto> findAllParties() {
        return IPartyRepository.findAll().stream().map(mapper::convertToDto).collect(Collectors.toList());
    }

    private boolean duplicityCheck(PartyDto partyDto, Party party) {
        boolean isTaxIdMatching = partyDto.getTaxId().equals(party.getTaxId());
        boolean isCompanyIdMatching = partyDto.getCompanyId().equals(party.getCompanyId());
        return isTaxIdMatching && isCompanyIdMatching;
    }

    public Party checkIfInvoiceExisting(UUID partyId) {
        Optional<Party> existingParty = IPartyRepository.findById(partyId);
        if (existingParty.isEmpty()) {
            throw new RuntimeException(String.format("Party with id %s doens't exists", partyId));
        } else {
            return existingParty.get();
        }
    }
}
