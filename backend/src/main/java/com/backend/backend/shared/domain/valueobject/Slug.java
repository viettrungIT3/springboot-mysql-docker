package com.backend.backend.shared.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * Value Object representing a URL-friendly slug.
 * Immutable and ensures business rules around slug format.
 */
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Slug {
    
    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    private static final int MAX_LENGTH = 180;
    private static final int MIN_LENGTH = 1;
    
    private String value;
    
    private Slug(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Slug cannot be null or empty");
        }
        
        String trimmedValue = value.trim();
        
        if (trimmedValue.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Slug must be at least " + MIN_LENGTH + " character long");
        }
        
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Slug cannot exceed " + MAX_LENGTH + " characters");
        }
        
        if (!SLUG_PATTERN.matcher(trimmedValue).matches()) {
            throw new IllegalArgumentException("Slug must contain only lowercase letters, numbers, and hyphens");
        }
        
        this.value = trimmedValue;
    }
    
    public static Slug of(String value) {
        return new Slug(value);
    }
    
    public static Slug fromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }
        
        String slug = text.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "") // Remove special characters
                .replaceAll("\\s+", "-") // Replace spaces with hyphens
                .replaceAll("-+", "-") // Replace multiple hyphens with single
                .replaceAll("^-|-$", ""); // Remove leading/trailing hyphens
        
        if (slug.isEmpty()) {
            throw new IllegalArgumentException("Cannot create slug from text: " + text);
        }
        
        // Truncate if too long
        if (slug.length() > MAX_LENGTH) {
            slug = slug.substring(0, MAX_LENGTH);
            // Remove trailing hyphen if exists
            if (slug.endsWith("-")) {
                slug = slug.substring(0, slug.length() - 1);
            }
        }
        
        return new Slug(slug);
    }
    
    public boolean isValid() {
        return value != null && 
               value.length() >= MIN_LENGTH && 
               value.length() <= MAX_LENGTH && 
               SLUG_PATTERN.matcher(value).matches();
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}
