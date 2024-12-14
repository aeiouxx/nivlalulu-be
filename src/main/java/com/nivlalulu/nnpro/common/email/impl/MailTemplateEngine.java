package com.nivlalulu.nnpro.common.email.impl;

import com.nivlalulu.nnpro.common.email.IMailTemplateEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class MailTemplateEngine implements IMailTemplateEngine {
    private final TemplateEngine templateEngine;

    @Override
    public String generatePasswordResetEmail(String token) {
        Context context = new Context();
        context.setVariable("token", token);
        return templateEngine.process("password_reset_email", context);
    }
}
