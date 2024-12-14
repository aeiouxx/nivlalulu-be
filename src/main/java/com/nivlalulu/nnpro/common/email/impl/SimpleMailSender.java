package com.nivlalulu.nnpro.common.email.impl;


import com.nivlalulu.nnpro.common.email.IMailSender;
import com.nivlalulu.nnpro.common.email.IMailTemplateEngine;
import lombok.RequiredArgsConstructor;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.email.EmailBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SimpleMailSender implements IMailSender {
    private final String from;
    private final Mailer mailer;
    private final IMailTemplateEngine mailTemplateEngine;

    public SimpleMailSender(
            @Value("${mail.host}")
            String host,
            @Value("${mail.port}")
            int port,
            @Value("${mail.username}")
            String username,
            @Value("${mail.password}")
            String password,
            @Value("${mail.from:${mail.username}}")
            String from,
            @Value("${mail.debug}")
            boolean withDebug,
            IMailTemplateEngine mailTemplateEngine
    ) {
        this.from = from;
        this.mailTemplateEngine = mailTemplateEngine;
        this.mailer = MailerBuilder
                .withSMTPServer(host, port, username, password)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(withDebug)
                .buildMailer();
    }


    @Override
    public void sendResetCode(String userEmail, String resetToken) {
        String body = mailTemplateEngine.generatePasswordResetEmail(resetToken);
        Email email = EmailBuilder.startingBlank()
                .from(from)
                .to(userEmail)
                .withSubject("Password reset")
                .withHTMLText(body)
                .buildEmail();
        mailer.sendMail(email);
    }
}