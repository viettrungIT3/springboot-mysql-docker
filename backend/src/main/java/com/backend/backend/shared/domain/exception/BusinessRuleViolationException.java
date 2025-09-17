package com.backend.backend.shared.domain.exception;

/**
 * Exception thrown when a business rule is violated.
 * This is a specific type of domain exception for business logic violations.
 */
public class BusinessRuleViolationException extends DomainException {
    
    private final String ruleName;
    
    public BusinessRuleViolationException(String ruleName, String message) {
        super(message);
        this.ruleName = ruleName;
    }
    
    public BusinessRuleViolationException(String ruleName, String message, Throwable cause) {
        super(message, cause);
        this.ruleName = ruleName;
    }
    
    public String getRuleName() {
        return ruleName;
    }
    
    @Override
    public String getMessage() {
        return String.format("Business rule violation [%s]: %s", ruleName, super.getMessage());
    }
}
