package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dto.v1.RefreshTokenResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IJwtTokenService {
    /**
     *  Rotates the refresh token and generates a new access token
     * @param request Request containing the current refresh token cookie
     * @param response Response will contain the rotated refresh token cookie
     * @return The new access token
     */
    RefreshTokenResponseDto refreshAndRotate(HttpServletRequest request, HttpServletResponse response);
}
