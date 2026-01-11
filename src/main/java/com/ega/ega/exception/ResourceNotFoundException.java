package com.ega.ega.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s introuvable avec %s : '%s'", resource, field, value));
    }
}
