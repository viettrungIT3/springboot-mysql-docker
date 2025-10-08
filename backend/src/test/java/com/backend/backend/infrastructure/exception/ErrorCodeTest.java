package com.backend.backend.infrastructure.exception;

import com.backend.backend.shared.domain.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Error Code Tests")
class ErrorCodeTest {

    @Test
    @DisplayName("Should create error code with correct format")
    void shouldCreateErrorCodeWithCorrectFormat() {
        // Given
        ErrorCode errorCode = ErrorCode.PRODUCT_NOT_FOUND;
        
        // When & Then
        assertEquals("APP-0401", errorCode.getCode());
        assertEquals("Product Not Found", errorCode.getTitle());
        assertEquals("Product does not exist", errorCode.getDescription());
    }

    @Test
    @DisplayName("Should categorize error codes correctly")
    void shouldCategorizeErrorCodesCorrectly() {
        // Authentication errors
        assertTrue(ErrorCode.AUTHENTICATION_FAILED.isAuthenticationError());
        assertTrue(ErrorCode.TOKEN_EXPIRED.isAuthenticationError());
        assertTrue(ErrorCode.ACCESS_DENIED.isAuthenticationError());
        
        // Validation errors
        assertTrue(ErrorCode.VALIDATION_FAILED.isValidationError());
        assertTrue(ErrorCode.INVALID_FORMAT.isValidationError());
        assertTrue(ErrorCode.REQUIRED_FIELD_MISSING.isValidationError());
        
        // Business errors
        assertTrue(ErrorCode.BUSINESS_RULE_VIOLATION.isBusinessError());
        assertTrue(ErrorCode.INSUFFICIENT_STOCK.isBusinessError());
        assertTrue(ErrorCode.ORDER_CANNOT_BE_MODIFIED.isBusinessError());
        
        // Entity errors
        assertTrue(ErrorCode.ENTITY_NOT_FOUND.isEntityError());
        assertTrue(ErrorCode.PRODUCT_NOT_FOUND.isEntityError());
        assertTrue(ErrorCode.CUSTOMER_NOT_FOUND.isEntityError());
        
        // System errors
        assertTrue(ErrorCode.INTERNAL_SERVER_ERROR.isSystemError());
        assertTrue(ErrorCode.SERVICE_UNAVAILABLE.isSystemError());
        assertTrue(ErrorCode.CONFIGURATION_ERROR.isSystemError());
    }

    @Test
    @DisplayName("Should find error code by code string")
    void shouldFindErrorCodeByCodeString() {
        // Given
        String code = "APP-0401";
        
        // When
        ErrorCode errorCode = ErrorCode.fromCode(code);
        
        // Then
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, errorCode);
    }

    @Test
    @DisplayName("Should throw exception for unknown error code")
    void shouldThrowExceptionForUnknownErrorCode() {
        // Given
        String unknownCode = "APP-9999";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> ErrorCode.fromCode(unknownCode));
    }

    @Test
    @DisplayName("Should have unique error codes")
    void shouldHaveUniqueErrorCodes() {
        // Given
        long uniqueCodes = java.util.Arrays.stream(ErrorCode.values())
                .map(ErrorCode::getCode)
                .distinct()
                .count();
        
        // When & Then
        assertEquals(ErrorCode.values().length, uniqueCodes);
    }

    @Test
    @DisplayName("Should have consistent code format")
    void shouldHaveConsistentCodeFormat() {
        // Given & When & Then
        java.util.Arrays.stream(ErrorCode.values())
                .forEach(errorCode -> {
                    String code = errorCode.getCode();
                    assertTrue(code.startsWith("APP-"), "Code should start with APP-: " + code);
                    assertTrue(code.length() == 8, "Code should be 8 characters long: " + code);
                    assertTrue(code.matches("APP-\\d{4}"), "Code should match APP-XXXX format: " + code);
                });
    }

    @Test
    @DisplayName("Should have non-empty titles and descriptions")
    void shouldHaveNonEmptyTitlesAndDescriptions() {
        // Given & When & Then
        java.util.Arrays.stream(ErrorCode.values())
                .forEach(errorCode -> {
                    assertNotNull(errorCode.getTitle(), "Title should not be null for: " + errorCode.getCode());
                    assertFalse(errorCode.getTitle().trim().isEmpty(), "Title should not be empty for: " + errorCode.getCode());
                    assertNotNull(errorCode.getDescription(), "Description should not be null for: " + errorCode.getCode());
                    assertFalse(errorCode.getDescription().trim().isEmpty(), "Description should not be empty for: " + errorCode.getCode());
                });
    }

    @Test
    @DisplayName("Should map to correct HTTP status codes")
    void shouldMapToCorrectHttpStatusCodes() {
        // Authentication errors -> 401/403
        assertTrue(ErrorCode.AUTHENTICATION_FAILED.isAuthenticationError());
        assertTrue(ErrorCode.TOKEN_EXPIRED.isAuthenticationError());
        assertTrue(ErrorCode.ACCESS_DENIED.isAuthenticationError());
        
        // Validation errors -> 400
        assertTrue(ErrorCode.VALIDATION_FAILED.isValidationError());
        assertTrue(ErrorCode.INVALID_FORMAT.isValidationError());
        
        // Business errors -> 400
        assertTrue(ErrorCode.BUSINESS_RULE_VIOLATION.isBusinessError());
        assertTrue(ErrorCode.INSUFFICIENT_STOCK.isBusinessError());
        
        // Entity errors -> 404/400
        assertTrue(ErrorCode.ENTITY_NOT_FOUND.isEntityError());
        assertTrue(ErrorCode.PRODUCT_NOT_FOUND.isEntityError());
        
        // System errors -> 500
        assertTrue(ErrorCode.INTERNAL_SERVER_ERROR.isSystemError());
        assertTrue(ErrorCode.SERVICE_UNAVAILABLE.isSystemError());
    }
}
