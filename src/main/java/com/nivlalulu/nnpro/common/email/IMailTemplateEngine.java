package com.nivlalulu.nnpro.common.email;

public interface IMailTemplateEngine {
    String generatePasswordResetEmail(String token);
}
