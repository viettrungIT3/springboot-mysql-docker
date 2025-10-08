package com.backend.backend.shared.domain.exception;

/**
 * User-specific exceptions with error codes
 */
public class UserException extends AppException {
    
    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public UserException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public UserException(ErrorCode errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }
    
    // Static factory methods for common user errors
    
    public static UserException notFound(Long userId) {
        return new UserException(ErrorCode.USER_NOT_FOUND, 
                "User with ID %d not found", userId);
    }
    
    public static UserException notFound(String username) {
        return new UserException(ErrorCode.USER_NOT_FOUND, 
                "User with username '%s' not found", username);
    }
    
    public static UserException alreadyExists(String username) {
        return new UserException(ErrorCode.ENTITY_ALREADY_EXISTS, 
                "User with username '%s' already exists", username);
    }
    
    public static UserException disabled(String username) {
        return new UserException(ErrorCode.USER_DISABLED, 
                "User '%s' is disabled", username);
    }
    
    public static UserException locked(String username) {
        return new UserException(ErrorCode.ACCOUNT_LOCKED, 
                "User account '%s' is locked", username);
    }
    
    public static UserException weakPassword() {
        return new UserException(ErrorCode.PASSWORD_TOO_WEAK, 
                "Password does not meet security requirements");
    }
    
    public static UserException invalidRole(String role) {
        return new UserException(ErrorCode.INVALID_ROLE, 
                "Invalid user role: %s", role);
    }
    
    public static UserException authenticationFailed(String username) {
        return new UserException(ErrorCode.AUTHENTICATION_FAILED, 
                "Authentication failed for user '%s'", username);
    }
    
    public static UserException accessDenied(String username, String resource) {
        return new UserException(ErrorCode.ACCESS_DENIED, 
                "User '%s' does not have access to resource '%s'", username, resource);
    }
}
