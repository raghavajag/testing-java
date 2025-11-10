package com.bank.util;

import org.springframework.stereotype.Component;

/**
 * Search Helper - Utility in attack paths
 */
@Component
public class SearchHelper {

    /**
     * Part of ATTACK PATH 2A and 2C
     * Pre-processes search queries but doesn't sanitize
     */
    public String preprocessSearchQuery(String query) {
        // Simple preprocessing that doesn't prevent SQL injection
        if (query == null) {
            return "";
        }
        
        // Trim whitespace (ineffective security)
        return query.trim();
    }

    /**
     * Part of ATTACK PATH 2C
     * Enriches search term with wildcards (makes SQL injection worse!)
     */
    public String enrichSearchTerm(String term) {
        // Add wildcards for "better" search (actually makes injection easier)
        if (term == null || term.isEmpty()) {
            return "%";
        }
        
        // Just passes through, preserving malicious input
        return term;
    }

    /**
     * Ineffective sanitization attempt
     */
    public String attemptSanitization(String input) {
        // This is insufficient - only removes obvious SQL keywords
        // but doesn't prevent encoded or obfuscated attacks
        if (input == null) {
            return null;
        }
        
        // Naive blacklist approach (easily bypassed)
        String sanitized = input.replaceAll("(?i)DROP", "");
        sanitized = sanitized.replaceAll("(?i)DELETE", "");
        
        // Still vulnerable to many injection techniques
        return sanitized;
    }
}
