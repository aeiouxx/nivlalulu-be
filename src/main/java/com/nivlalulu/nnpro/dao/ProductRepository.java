package com.nivlalulu.nnpro.dao;

import com.nivlalulu.nnpro.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findById(UUID id);

    List<Product> findAllByPrice(BigDecimal price);

    List<Product> findAllByNameContaining(String name);

    List<Product> findByIdIn(List<UUID> productLists);

    boolean existsByNameAndPrice(String name, BigDecimal price);

}