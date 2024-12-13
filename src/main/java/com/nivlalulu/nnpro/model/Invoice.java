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

    @Column(nullable = false)
    private String variableSymbol;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InvoiceItem> invoiceItemList;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Party customer;

    @ManyToOne
    @JoinColumn(name = "supplier_id", referencedColumnName = "id")
    private Party supplier;

    @Column
    private String contactPerson;

    public Invoice(Timestamp created, Timestamp expiration, PaymentMethod paymentMethod, String variableSymbol, Set<InvoiceItem> invoiceItemList, Party customer, Party supplier) {
        this.created = created;
        this.expiration = expiration;
        this.paymentMethod = paymentMethod;
        this.variableSymbol = variableSymbol;
        this.invoiceItemList = invoiceItemList;
        this.customer = customer;
        this.supplier = supplier;
    }
}
