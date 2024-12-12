package com.nivlalulu.nnpro.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Data
// todo: Party dto
public class UserDto {

    @JsonIgnore
    private Long id;

    private String username;

    private BigInteger phone;

    private String email;

}
