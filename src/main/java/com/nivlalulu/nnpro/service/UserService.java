package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dao.UserRepository;
import com.nivlalulu.nnpro.dto.UserDto;
import com.nivlalulu.nnpro.model.User;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public Pair<Optional<User>, String> deleteUser(String name) {
        Optional<User> existingUser = findUserByUsername(name);
        if (existingUser.isEmpty()) {
            return Pair.of(Optional.empty(), "User with this name doesn't exists");
        }

        User user = existingUser.get();

        userRepository.delete(user);
        return Pair.of(Optional.of(user), StringUtils.EMPTY);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> login = userRepository.findByUsername(username);
        if (login.isEmpty()) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return login.get();
    }

    public void validateUser(UserDto userDto) {
        if (userDto.getCompanyId() == null) {
            throw new RuntimeException("company id is null");
        }
        if (userDto.getCountry() == null) {
            throw new RuntimeException("country is null");
        }
        if (userDto.getPhone() == null) {
            throw new RuntimeException("country is null");
        }
        if (userDto.getAddress() == null) {
            throw new RuntimeException("address is null");
        }
        if (userDto.getEmail() == null) {
            throw new RuntimeException("email is null");
        }
        if (userDto.getContactPerson() == null) {
            throw new RuntimeException("name is null");
        }
        if (userDto.getTaxId() == null) {
            throw new RuntimeException("tax id is null");
        }
        if (userDto.getOrganizationName() == null) {
            throw new RuntimeException("company id is null");
        }
    }
}
