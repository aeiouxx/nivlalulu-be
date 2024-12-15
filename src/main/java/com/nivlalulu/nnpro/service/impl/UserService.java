package com.nivlalulu.nnpro.service.impl;

import com.nivlalulu.nnpro.common.exceptions.NotFoundException;
import com.nivlalulu.nnpro.dto.v1.UserDto;
import com.nivlalulu.nnpro.model.User;
import com.nivlalulu.nnpro.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final IUserRepository IUserRepository;

    public UserDto findUserDtoById(Long id) {
        User user = checkIfUserExisting(id);
        return MappingService.convertToDto(user);
    }

    public User findUserById(Long id) {
        return checkIfUserExisting(id);
    }

    protected Optional<User> findInvoiceById(Long id) {
        return IUserRepository.findById(id);
    }

    private User checkIfUserExisting(Long id) {
        Optional<User> existingUser = findInvoiceById(id);
        if (existingUser.isEmpty()) {
            throw new NotFoundException("User", "id", id.toString());
        } else {
            return existingUser.get();
        }
    }
}
