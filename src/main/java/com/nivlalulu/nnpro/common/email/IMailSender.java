package com.nivlalulu.nnpro.common.email;

public interface IMailSender {
    /**
     * Sends an email containing the reset token to the user
     * @param userEmail The email of the user
     * @param resetToken The reset token
     */
    void sendResetCode(String userEmail, String resetToken);
}
