package com.nivlalulu.nnpro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "parties")
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ic_tax", "user_id"}),
                @UniqueConstraint(columnNames = {"dic_tax", "user_id"})
        }
)
public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String email;

    @Column(name = "ic_tax", nullable = true)
    private String icTax;

    @Column(name = "dic_tax", nullable = true)
    private String dicTax;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public Party(String name,
                 String address,
                 String country,
                 String icTax,
                 String dicTax,
                 String telephone,
                 String email,
                 User user) {
        this.name = name;
        this.address = address;
        this.country = country;
        this.icTax = icTax;
        this.dicTax = dicTax;
        this.telephone = telephone;
        this.email = email;
        this.user = user;
    }
}
