package com.nivlalulu.nnpro.common.email;

public interface IMailTemplateEngine {
    /**
     * Generates the email body for a password reset getEmail
     * @param token The reset token
     * @return The email body
     */
    String generatePasswordResetEmail(String token);
}
