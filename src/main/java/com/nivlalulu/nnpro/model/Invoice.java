package com.nivlalulu.nnpro.model;

import com.nivlalulu.nnpro.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "invoices")
@NamedEntityGraph(
        name = Invoice.WITH_ITEMS_GRAPH,
        attributeNodes = @NamedAttributeNode("items"))
public class Invoice {
    public static final String WITH_ITEMS_GRAPH = "invoice-with-items";
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt;

    @Column(name="expires_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant expiresAt;

    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private String variableSymbol;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InvoiceItem> items = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Party customer;

    @ManyToOne
    @JoinColumn(name = "supplier_id", referencedColumnName = "id")
    private Party supplier;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "contact")
    private String contact;

    public Invoice(Instant created,
                   Instant expiration,
                   PaymentMethod paymentMethod,
                   String variableSymbol,
                   Set<InvoiceItem> items,
                   Party customer,
                   Party supplier,
                   String contact,
                   User user) {
        this.createdAt = created;
        this.expiresAt = expiration;
        this.paymentMethod = paymentMethod;
        this.variableSymbol = variableSymbol;
        this.items = items;
        this.customer = customer;
        this.supplier = supplier;
        this.contact = contact;
        this.user = user;
    }
}
