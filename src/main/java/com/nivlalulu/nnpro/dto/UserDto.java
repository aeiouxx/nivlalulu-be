package com.nivlalulu.nnpro.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private String organizationName;

    private String fullname;

    private String email;

    private String address;

    private String country;

    private BigInteger phone;

    private String companyId;

    private String taxId;


}
