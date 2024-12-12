package com.nivlalulu.nnpro.dao;

import com.nivlalulu.nnpro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAllByOrganizationName(String organizationName);

}
