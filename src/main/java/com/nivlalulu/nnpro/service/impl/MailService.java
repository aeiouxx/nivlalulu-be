package com.nivlalulu.nnpro.service.impl;


import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.email.EmailBuilder;

public class MailService {

    private void sendEmail(String userEmail, String resetCode) {
        Email email = EmailBuilder.startingBlank()
                .from("")
                .to(userEmail)
                .withSubject("Reset code")
                .withPlainText("Reset code: " + resetCode)
                .buildEmail();


        Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "", "")
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer();

        mailer.sendMail(email);
    }
}
