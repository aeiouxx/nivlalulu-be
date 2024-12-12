package com.nivlalulu.nnpro.common.exceptions;

public class NotFoundException extends RuntimeException {
    private Class<?> Type;
    public NotFoundException(Class<?> type) {
        super("Entity of type " + type.getName() + " not found");
        Type = type;
    }
    public NotFoundException(String resourceName,String fieldName, String fieldValue) {
        super(String.format("%s with %s '%s' doesn't exist", resourceName, fieldName, fieldValue));
    }
    public static NotFoundException create(Class<?> type) {
        return new NotFoundException(type);
    }
}
