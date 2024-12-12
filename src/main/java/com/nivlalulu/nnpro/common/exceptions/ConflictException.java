package com.nivlalulu.nnpro.common.exceptions;


public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s with %s '%s' already exists", resourceName, fieldName, fieldValue));
    }
}
