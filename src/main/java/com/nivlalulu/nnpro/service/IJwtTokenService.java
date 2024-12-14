package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dto.v1.RefreshTokenResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Service for managing JWT tokens
 */
public interface IJwtTokenService {
    /**
     *  Rotates the refresh token and generates a new access token, assumes the request information
     *  is up to date with the current user state
     * @param request Request containing the current refresh token cookie
     * @param response Response will contain the rotated refresh token cookie
     * @return The new access token
     */
    RefreshTokenResponseDto refreshAndRotate(HttpServletRequest request, HttpServletResponse response);

    /**
     *  Rotates the refresh token and generates a new access token, useful when the request information
     *  is different from the current user state (e.g. changing username)
     * @param username The username of the user (is different from the username in the jwt token)
     * @param request Request containing the current refresh token cookie
     * @param response Response will contain the rotated refresh token cookie
     * @return The new access token
     */
    String refreshAndRotate(String username, HttpServletRequest request, HttpServletResponse response);
}
