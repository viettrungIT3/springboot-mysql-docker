package com.backend.backend.shared.domain.exception;

/**
 * Base exception for all domain-related errors.
 * This exception should be used for business rule violations and domain logic errors.
 */
public class DomainException extends RuntimeException {
    
    public DomainException(String message) {
        super(message);
    }
    
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DomainException(Throwable cause) {
        super(cause);
    }
}
