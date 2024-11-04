package com.nivlalulu.nnpro.dao;

import com.nivlalulu.nnpro.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findAllByPrice(BigDecimal price);

    List<Product> findAllByNameContaining(String name);

}