package com.nivlalulu.nnpro.model;

import com.nivlalulu.nnpro.enums.PaymentMethod;
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

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private UUID invoiceNumber;

    @Column(nullable = false)
    private Timestamp created;

    @Column(nullable = false)
    private Timestamp expiration;

    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product")
    private List<Product> productList;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User supplier;

    public Invoice(Timestamp created, Timestamp expiration, PaymentMethod paymentMethod, List<Product> productList, User customer, User supplier) {
        this.created = created;
        this.expiration = expiration;
        this.paymentMethod = paymentMethod;
        this.productList = productList;
        this.customer = customer;
        this.supplier = supplier;
    }
}
