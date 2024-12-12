package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dto.v1.AuthenticationResponseDto;
import com.nivlalulu.nnpro.dto.v1.LoginRequestDto;
import com.nivlalulu.nnpro.dto.v1.RegistrationRequestDto;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {
    /**
     * Logs in a user
     * @param login
     * @param response
     * @return
     */
    public AuthenticationResponseDto login(LoginRequestDto login, HttpServletResponse response);

    /**
     * Registers a new user
     * @param registration
     * @param response
     * @return
     */
    public AuthenticationResponseDto register(RegistrationRequestDto registration, HttpServletResponse response);
}
