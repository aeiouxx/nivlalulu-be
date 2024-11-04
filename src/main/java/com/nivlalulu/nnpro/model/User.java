package com.nivlalulu.nnpro.model;

import com.nivlalulu.nnpro.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'USER'")
    private Role role;

    @Column(nullable = false)
    private String organizationName;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "invoice")
    private List<Invoice> invoiceList;

}
