package com.nivlalulu.nnpro.security.ownership.checkers;

import com.nivlalulu.nnpro.model.Party;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IPartyRepository;
import com.nivlalulu.nnpro.security.ownership.IOwnershipChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PartyOwnershipChecker implements IOwnershipChecker<UUID> {
    private final IPartyRepository partyRepository;

    @Override
    public Class<?> getEntityClass() {
        return Party.class;
    }

    @Override
    public boolean resourceExists(UUID resourceId) {
        return partyRepository.existsById(resourceId);
    }

    @Override
    public boolean isOwnedBy(UUID resourceId, User user) {
        return partyRepository.existsByIdAndUser(resourceId, user);
    }
}
