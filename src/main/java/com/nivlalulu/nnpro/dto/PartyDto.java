package com.nivlalulu.nnpro.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PartyDto {

    @JsonIgnore
    private UUID id;

    private String organizationName;

    private String personName;

    private String address;

    private String country;

    private String companyId;

    private String taxId;

    private String telephone;

    private String email;

}
