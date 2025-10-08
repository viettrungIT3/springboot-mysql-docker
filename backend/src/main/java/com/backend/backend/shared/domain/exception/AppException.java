package com.backend.backend.shared.domain.exception;

/**
 * Base application exception with error code support.
 * All application-specific exceptions should extend this class.
 */
public class AppException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final Object[] args;
    
    public AppException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.args = new Object[0];
    }
    
    public AppException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }
    
    public AppException(ErrorCode errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }
    
    public AppException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getDescription(), cause);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }
    
    public AppException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public Object[] getArgs() {
        return args;
    }
    
    public String getFormattedMessage() {
        if (args.length == 0) {
            return getMessage();
        }
        return String.format(getMessage(), args);
    }
}
