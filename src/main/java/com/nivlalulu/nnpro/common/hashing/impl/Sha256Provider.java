package com.nivlalulu.nnpro.common.hashing.impl;

import com.nivlalulu.nnpro.common.hashing.IHashProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
@Slf4j
public class Sha256Provider implements IHashProvider {
    private static final String DIGEST_ALGORITHM = "SHA-256";

    public String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
            var encoded = digest.digest( input.getBytes(StandardCharsets.UTF_8) );
            return bytesToHex(encoded);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error while hashing.", e);
            // don't leak the actual cause to the client
            throw new RuntimeException("Internal error occurred.");
        }
    }

    public String hmac(String input, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] derivedBytes = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(derivedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while hashing with HMAC SHA-256.", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = String.format("%02x", b);
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
