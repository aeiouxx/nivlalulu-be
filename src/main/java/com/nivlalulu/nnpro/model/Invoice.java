package com.nivlalulu.nnpro.model;

import com.nivlalulu.nnpro.dto.v1.PartySnapshotDto;
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
        name = Invoice.WITH_ITEMS_AND_PARTIES_GRAPH,
        attributeNodes = @NamedAttributeNode("items"))
public class Invoice {
    public static final String WITH_ITEMS_AND_PARTIES_GRAPH = "invoice-with-items-and-parties";
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Party customer;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "supplier_id", referencedColumnName = "id")
    private Party supplier;

    // This data is denormalized on purpose,
    // we want to keep a "snapshot" of the supplier / customer information
    // at the time of the invoice creation.
    // If we edit the supplier / customer information, the invoice should not be affected (unless we want it to be).
    // ----------------------------------------------------------------------------------------------------
    @Column(name = "supplier_name", nullable = false)
    private String supplierName;
    @Column(name = "supplier_address", nullable = false)
    private String supplierAddress;
    @Column(name = "supplier_country", nullable = false)
    private String supplierCountry;
    @Column(name = "supplier_ic_tax", nullable = false)
    private String supplierIcTax;
    @Column(name = "supplier_dic_tax", nullable = false)
    private String supplierDicTax;
    @Column(name = "supplier_telephone", nullable = false)
    private String supplierTelephone;
    @Column(name = "supplier_email", nullable = false)
    private String supplierEmail;

    @Column(name = "customer_name", nullable = false)
    private String customerName;
    @Column(name = "customer_address", nullable = false)
    private String customerAddress;
    @Column(name = "customer_country", nullable = false)
    private String customerCountry;
    @Column(name = "customer_ic_tax", nullable = false)
    private String customerIcTax;
    @Column(name = "customer_dic_tax", nullable = false)
    private String customerDicTax;
    // ----------------------------------------------------------------------------------------------------

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

        snapshotSupplier(supplier);
        snapshotCustomer(customer);
    }

    public void snapshotSupplier(Party supplier) {
        this.supplierName = supplier.getName();
        this.supplierAddress = supplier.getAddress();
        this.supplierCountry = supplier.getCountry();
        this.supplierIcTax = supplier.getIcTax();
        this.supplierDicTax = supplier.getDicTax();
        this.supplierTelephone = supplier.getTelephone();
        this.supplierEmail = supplier.getEmail();
    }

    public void snapshotCustomer(Party customer) {
        this.customerName = customer.getName();
        this.customerAddress = customer.getAddress();
        this.customerCountry = customer.getCountry();
        this.customerIcTax = customer.getIcTax();
        this.customerDicTax = customer.getDicTax();
    }
}
