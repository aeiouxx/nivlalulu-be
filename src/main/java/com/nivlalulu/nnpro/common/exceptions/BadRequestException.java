package com.nivlalulu.nnpro.common.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
    public BadRequestException(String resourceName, String issue) {
        super(String.format("Invalid request for %s: %s", resourceName, issue));
    }
}
