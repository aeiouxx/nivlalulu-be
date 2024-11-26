package com.nivlalulu.nnpro.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {

    @JsonIgnore
    private UUID id;

    private String type;

    @JsonProperty("organization_name")
    private String organizationName;

    @JsonProperty("contact_person")
    private String contactPerson;

    private String address;

    private String country;

    private String companyId;

    private String taxId;

    private BigInteger phone;

    private String email;

}
