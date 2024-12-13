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
    private List<Invoice> customerInvoices;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private List<Invoice> supplierInvoices;

    public Party(String organizationName, String personName, String address, String country, String companyId, String taxId, String telephone, String email, List<Invoice> customerInvoices, List<Invoice> supplierInvoices) {
        this.organizationName = organizationName;
        this.personName = personName;
        this.address = address;
        this.country = country;
        this.companyId = companyId;
        this.taxId = taxId;
        this.telephone = telephone;
        this.email = email;
        this.customerInvoices = customerInvoices;
        this.supplierInvoices = supplierInvoices;
    }

    public Party(String organizationName, String personName, String address, String country, String companyId, String taxId, String telephone, String email) {
        this.organizationName = organizationName;
        this.personName = personName;
        this.address = address;
        this.country = country;
        this.companyId = companyId;
        this.taxId = taxId;
        this.telephone = telephone;
        this.email = email;
        this.customerInvoices = new ArrayList<>();
        this.supplierInvoices = new ArrayList<>();
    }
}
