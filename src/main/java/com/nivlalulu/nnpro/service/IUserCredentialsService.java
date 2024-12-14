package com.nivlalulu.nnpro.service;

/**
 * Service for managing changes to user credentials * (password, email, username).
 */
public interface IUserCredentialsService {
    // todo: allow username or email?
    void createPasswordResetToken(String username);
    void resetPassword(String token, String newPassword);
    void changePassword(String username, String oldPassword, String newPassword);
}
