package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.exceptions.NotFoundException;
import com.nivlalulu.nnpro.common.mapping.impl.GenericModelMapper;
import com.nivlalulu.nnpro.dto.v1.PartyDto;
import com.nivlalulu.nnpro.dto.v1.UserDto;
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
    public PartyDto createParty(PartyDto partyDto, UserDto userDto) {
        Optional<Party> existingParty = partyRepository.findByTaxIdOrCompanyIdAndUserId(partyDto.getTaxId(),
                partyDto.getCompanyId(), userDto.getId());
        if (!isUserIdMatching(partyDto.getUserId(), userDto.getId())) {
            throw new RuntimeException("User id isn't matching");
        }
        if (existingParty.isPresent()) {
            if (!isUserIdMatching(existingParty.get().getUser().getId(), userDto.getId())) {
                throw new RuntimeException("User id isn't matching");
            }
            if (!duplicityCheck(partyDto, existingParty.get())) {
                User user = userService.findUserById(existingParty.get().getUser().getId());
                user.getParties().add(existingParty.get());
                userRepository.save(user);
                mapper.convertToDto(partyRepository.save(existingParty.get()));
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
                userService.findUserById(userDto.getId()));
        return mapper.convertToDto(partyRepository.save(party));
    }

    @Override
    public PartyDto updateParty(PartyDto partyDto, UserDto userDto) {
        Party party = partyRepository.findById(partyDto.getId())
                .orElseThrow(() -> new NotFoundException("Party", "id", partyDto.getId().toString()));

        if (!isUserIdMatching(party.getUser().getId(), userDto.getId())) {
            throw new RuntimeException("User isn't linked to party");
        }

        party.setOrganizationName(partyDto.getOrganizationName());
        party.setPersonName(partyDto.getPersonName());
        party.setAddress(partyDto.getAddress());
        party.setCountry(partyDto.getCountry());
        party.setCompanyId(partyDto.getCompanyId());
        party.setTaxId(partyDto.getTaxId());
        party.setTelephone(partyDto.getTelephone());
        party.setEmail(partyDto.getEmail());

        //TODO atdy možná může být problem tou vazbou v Party entita
        return mapper.convertToDto(partyRepository.save(party));
    }

    @Override
    public PartyDto deleteParty(UUID id, UserDto userDto) {
        Party party = partyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Party", "id", id.toString()));

        if (!isUserIdMatching(party.getUser().getId(), userDto.getId())) {
            throw new RuntimeException("User isn't linked to party");
        }

        if (!party.getCustomerInvoices().isEmpty()) {
            throw new RuntimeException("Can't delete party because is in some Invoice as customer!");
        }
        if (!party.getSupplierInvoices().isEmpty()) {
            throw new RuntimeException("Can't delete party because is in some Invoice as supplier!");
        }

        User user = userService.findUserById(userDto.getId());
        user.getInvoiceItems().remove(user);
        userRepository.save(user);

        partyRepository.delete(party);
        return mapper.convertToDto(party);
    }

    @Override
    public PartyDto findById(UUID id, UserDto userDto) {
        Party party = checkIfInvoiceExisting(id);
        if (isUserIdMatching(party.getUser().getId(), userDto.getId())) {
            return mapper.convertToDto(party);
        }
        throw new RuntimeException("Party isn't linked to this user");
    }

    @Override
    public Optional<PartyDto> findByTaxId(String taxId, UserDto userDto) {
        Optional<Party> party = partyRepository.findByTaxIdAndUserId(taxId, userDto.getId());
        return party.map(value -> Optional.of(mapper.convertToDto(value))).orElse(null);
    }

    @Override
    public Optional<PartyDto> findByCompanyId(String companyId, UserDto userDto) {
        Optional<Party> party = partyRepository.findByCompanyIdAndUserId(companyId, userDto.getId());
        return party.map(value -> Optional.of(mapper.convertToDto(value))).orElse(null);
    }

    @Override
    public List<PartyDto> findAllParties() {
        return partyRepository.findAll()
                .stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PartyDto> findUserParties(UserDto userDto) {
        return partyRepository.findPartiesByUser(userDto.getId())
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
        return partyRepository.findById(partyId)
                .orElseThrow(() -> {
                    log.error("Party with id {} doesn't exist.", partyId);
                    return new NotFoundException(Party.class);
                });
    }

    private boolean isUserIdMatching(Long id, Long userId) {
        return id.equals(userId);
    }


}
