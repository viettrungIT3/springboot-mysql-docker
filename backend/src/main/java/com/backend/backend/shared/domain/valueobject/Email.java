package com.backend.backend.shared.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * Value Object representing an email address.
 * Immutable and ensures business rules around email format.
 */
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Email {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    private static final int MAX_LENGTH = 255;
    
    private String value;
    
    private Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        String trimmedValue = value.trim().toLowerCase();
        
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Email cannot exceed " + MAX_LENGTH + " characters");
        }
        
        if (!EMAIL_PATTERN.matcher(trimmedValue).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
        
        this.value = trimmedValue;
    }
    
    public static Email of(String value) {
        return new Email(value);
    }
    
    public boolean isValid() {
        return value != null && 
               value.length() <= MAX_LENGTH && 
               EMAIL_PATTERN.matcher(value).matches();
    }
    
    public String getValue() {
        return value;
    }
    
    public String getDomain() {
        return value.substring(value.indexOf("@") + 1);
    }
    
    public String getLocalPart() {
        return value.substring(0, value.indexOf("@"));
    }
    
    @Override
    public String toString() {
        return value;
    }
}
