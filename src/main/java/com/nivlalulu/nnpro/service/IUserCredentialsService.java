package com.nivlalulu.nnpro.service;

/**
 * Service for managing changes to user credentials * (password, email, username).
 */
public interface IUserCredentialsService {
    /**
     * Creates a password reset token for the specified user and sends it via email.
     * @param username
     */
    void createAndSendPasswordResetToken(String username);
    /**
     * Resets the user's password using the provided token.
     * @param token
     * @param newPassword
     */
    void resetPassword(String token, String newPassword);
    /**
     * Changes the password for the specified user.
     * @param username
     * @param oldPassword
     * @param newPassword
     */
    void changePassword(String username, String oldPassword, String newPassword);
    /**
     * Changes the username for the specified user.
     * @param username
     * @param newUsername
     * @return the new username
     */
    String changeUsername(String username, String newUsername);
    /**
     * Changes the email for the specified user.
     * @param username
     * @param newEmail
     * @return the new email
     */
    String changeEmail(String username, String newEmail);
}
