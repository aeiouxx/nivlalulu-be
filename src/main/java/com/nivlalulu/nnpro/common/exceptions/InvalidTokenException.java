package com.nivlalulu.nnpro.common.exceptions;


// todo: Should return cause
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
