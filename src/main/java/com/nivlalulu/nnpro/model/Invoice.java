package com.nivlalulu.nnpro.model;

import com.nivlalulu.nnpro.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID invoiceNumber;

    @Column(nullable = false)
    private Timestamp created;

    @Column(nullable = false)
    private Timestamp expiration;

    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToMany
    @JoinTable(
            name = "invoices_products",
            joinColumns = @JoinColumn(name = "invoice_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<InvoiceItem> invoiceItemList;


    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private User customer;

    @Column
    private String customerOrganizationName;

    @Column
    private String customerAddress;

    @Column
    private String customerCountry;

    @Column
    private String customerCompanyId;

    @Column
    private String customerTaxId;

    @ManyToOne
    @JoinColumn(name = "supplier_id", referencedColumnName = "id")
    private User supplier;

    @Column
    private String supplierOrganizationName;

    @Column
    private String supplierAddress;

    @Column
    private String supplierCountry;

    @Column
    private String contactPerson;

    @Column
    private String supplierCompanyId;

    @Column
    private String supplierTaxId;

    public Invoice(Timestamp created, Timestamp expiration, PaymentMethod paymentMethod, Set<InvoiceItem> invoiceItemList, User customer, User supplier) {
        this.created = created;
        this.expiration = expiration;
        this.paymentMethod = paymentMethod;
        this.invoiceItemList = invoiceItemList;
        this.customer = customer;
        this.supplier = supplier;
    }
}
