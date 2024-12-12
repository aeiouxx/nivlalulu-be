package com.nivlalulu.nnpro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal taxPrice;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @ManyToMany(mappedBy = "productList")
    private Set<Invoice> invoice;

    public Product(String name, Integer quantity, BigDecimal price, BigDecimal taxPrice, BigDecimal totalPrice) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.taxPrice = taxPrice;
        this.totalPrice = totalPrice;
    }
}
