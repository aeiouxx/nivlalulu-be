package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.exceptions.NotFoundException;
import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import com.nivlalulu.nnpro.dto.v1.PartyDto;
import com.nivlalulu.nnpro.model.Party;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IPartyRepository;
import com.nivlalulu.nnpro.repository.IUserRepository;
import com.nivlalulu.nnpro.service.IPartyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyService implements IPartyService {
    private final IPartyRepository partyRepository;
    private final IUserRepository userRepository;
    private final UserService userService;
    private final GenericModelMapper mapper;

    @Override
    public PartyDto createParty(PartyDto partyDto, User user) {
        Optional<Party> existingParty = partyRepository.findByTaxIdOrCompanyId(partyDto.getTaxId(),
                partyDto.getCompanyId());
        if (existingParty.isPresent()) {
            if (!duplicityCheck(partyDto, existingParty.get())) {
//                User user = userService.findUserById(existingParty.get().getUser().getId());
//                user.getParties().add(existingParty.get());
//                userRepository.save(user);
//                mapper.convertToDto(partyRepository.save(existingParty.get()));
            } else {
                return mapper.convertToDto(existingParty.get());
            }
        }

        Party party = new Party(
                partyDto.getOrganizationName(),
                partyDto.getPersonName(),
                partyDto.getAddress(),
                partyDto.getCountry(),
                partyDto.getCompanyId(),
                partyDto.getTaxId(),
                partyDto.getTelephone(),
                partyDto.getEmail(),
                user);
        var created = partyRepository.save(party);
        return mapper.convertToDto(created);
    }

    @Override
    public PartyDto updateParty(PartyDto partyUpdated, User user) {
        var party = partyRepository.findByIdAndUser(partyUpdated.getId(), user)
                .orElseThrow(() -> new NotFoundException("Party", "id", partyUpdated.getId().toString()));

        if (partyUpdated.getOrganizationName() != null) {
            party.setOrganizationName(partyUpdated.getOrganizationName());
        }
        if (partyUpdated.getPersonName() != null) {
            party.setPersonName(partyUpdated.getPersonName());
        }
        if (partyUpdated.getAddress() != null) {
            party.setAddress(partyUpdated.getAddress());
        }
        if (partyUpdated.getCountry() != null) {
            party.setCountry(partyUpdated.getCountry());
        }
        if (partyUpdated.getCompanyId() != null) {
            party.setCompanyId(partyUpdated.getCompanyId());
        }
        if (partyUpdated.getTaxId() != null) {
            party.setTaxId(partyUpdated.getTaxId());
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
    public Optional<PartyDto> findByTaxId(String taxId) {
        Optional<Party> party = partyRepository.findByTaxId(taxId);
        return party.map(value -> Optional.of(mapper.convertToDto(value))).orElse(null);
    }

    @Override
    public Optional<PartyDto> findByCompanyId(String companyId) {
        Optional<Party> party = partyRepository.findByCompanyId(companyId);
        return party.map(value -> Optional.of(mapper.convertToDto(value))).orElse(null);
    }

    @Override
    public List<PartyDto> findAllParties() {
        return partyRepository.findAll()
                .stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toList());
    }

    private boolean duplicityCheck(PartyDto partyDto, Party party) {
        boolean isTaxIdMatching = partyDto.getTaxId().equals(party.getTaxId());
        boolean isCompanyIdMatching = partyDto.getCompanyId().equals(party.getCompanyId());
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
