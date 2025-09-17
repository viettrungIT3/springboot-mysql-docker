package com.backend.backend.shared.domain.exception;

/**
 * Exception thrown when an entity is not found.
 * This is a specific type of domain exception for entity lookup failures.
 */
public class EntityNotFoundException extends DomainException {
    
    private final String entityType;
    private final Object identifier;
    
    public EntityNotFoundException(String entityType, Object identifier) {
        super(String.format("%s with identifier '%s' not found", entityType, identifier));
        this.entityType = entityType;
        this.identifier = identifier;
    }
    
    public EntityNotFoundException(String entityType, Object identifier, String message) {
        super(String.format("%s with identifier '%s' not found: %s", entityType, identifier, message));
        this.entityType = entityType;
        this.identifier = identifier;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public Object getIdentifier() {
        return identifier;
    }
}
