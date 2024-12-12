package com.nivlalulu.nnpro.repository;

import com.nivlalulu.nnpro.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findById(UUID id);

    List<Product> findAllByPrice(BigDecimal price);

    List<Product> findAllByNameContaining(String name);

    List<Product> findByIdIn(List<UUID> productLists);

    boolean existsByNameAndPrice(String name, BigDecimal price);

    Optional<Product> findProductByNameAndPrice(String name, BigDecimal price);

}