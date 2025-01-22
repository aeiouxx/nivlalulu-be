package com.nivlalulu.nnpro.service;

import com.nivlalulu.nnpro.dto.v1.TokenDto;
import com.nivlalulu.nnpro.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Service for managing JWT tokens
 */
public interface IJwtTokenService {

    /**
     *  Generates a new access token, assumes the request information
     *  is up to date with the current user state
     * @param request Request containing the current refresh token cookie
     * @return The new access token
     */
     TokenDto refresh(HttpServletRequest request);

    /**
     *  Generates a new access token and invalidates the refresh token, useful when the request information
     *  is different from the current user state (e.g. changing username)
     *
     *  If we simply rotate the refresh token, we would be vulnerable as a single access token would grant
     *  infinite login time (could just keep periodically changing our username while logged in).
     *
     *  FE doesn't want to implement protected account change so we simply invalidate the refresh cookie...
     * @param username The username of the user (is different from the username in the jwt token)
     * @param request Request containing the current refresh token cookie
     * @param response Response will contain the invalidated refresh token cookie
     * @return The new access token
     */
    TokenDto refreshAndInvalidate(String username,
                                HttpServletRequest request,
                                HttpServletResponse response);

    /**
     * Logs the user out by invalidating the refresh token (we should probably blacklist the access token as well)
     * @param user
     * @param request
     * @param response
     */
    void logout(User user,
                HttpServletRequest request,
                HttpServletResponse response);
}
