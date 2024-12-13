package com.nivlalulu.nnpro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "parties")
public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(nullable = true)
    private String organizationName;

    @Column(nullable = true)
    private String personName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String country;

    @Column(nullable = true, unique = true)
    private String companyId;

    @Column(nullable = true, unique = true)
    private String taxId;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String email;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Invoice> customerInvoices = new ArrayList<>();

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private List<Invoice> supplierInvoices = new ArrayList<>();

}
