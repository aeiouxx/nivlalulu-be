package com.nivlalulu.nnpro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String companyId; //ičo

    @Column(nullable = false)
    private String taxId; //dič

    @Column(nullable = false)
    private Timestamp created;

    @Column(nullable = false)
    private Timestamp expiration;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product")
    private List<Product> productList;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Invoice(String companyName, String companyId, String taxId, Timestamp created, Timestamp expiration, List<Product> productList, User user) {
        this.companyName = companyName;
        this.companyId = companyId;
        this.taxId = taxId;
        this.created = created;
        this.expiration = expiration;
        this.productList = productList;
        this.user = user;
    }
}
