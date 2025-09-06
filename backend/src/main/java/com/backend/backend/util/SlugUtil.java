package com.backend.backend.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility class for generating URL-friendly slugs from text.
 * Converts text to lowercase, removes special characters, and replaces spaces with hyphens.
 */
public class SlugUtil {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    /**
     * Converts input text to a URL-friendly slug.
     * 
     * @param input the text to convert
     * @return the generated slug
     */
    public static String toSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        
        // Replace whitespace with hyphens
        String nowhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        
        // Normalize unicode characters
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        
        // Remove non-latin characters except hyphens
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        
        // Convert to lowercase and clean up multiple hyphens
        return slug.toLowerCase(Locale.ENGLISH)
                .replaceAll("-{2,}", "-")  // Replace multiple hyphens with single hyphen
                .replaceAll("^-+|-+$", ""); // Remove leading and trailing hyphens
    }
}
