package com.nivlalulu.nnpro.repository;

import com.nivlalulu.nnpro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);

}
