package com.bank.util;

import org.springframework.stereotype.Component;

/**
 * Mobile Helper - Utility in mobile attack paths
 */
@Component
public class MobileHelper {

    /**
     * Part of ATTACK PATH 3A
     * Formats query for mobile display
     */
    public String formatForMobile(String query) {
        // Format for mobile display (doesn't sanitize)
        if (query == null) {
            return "";
        }
        
        // Just passes through
        return query;
    }

    /**
     * Part of ATTACK PATH 3A
     * Optimizes query (but doesn't sanitize)
     */
    public String optimizeSearchQuery(String query) {
        // Optimization that doesn't prevent SQL injection
        if (query == null || query.isEmpty()) {
            return "";
        }
        
        // Collapse multiple spaces
        return query.replaceAll("\\s+", " ").trim();
    }

    /**
     * Part of ATTACK PATH 3C
     * Session verification (separate from SQL injection)
     */
    public boolean verifySession(String sessionToken) {
        // Simplified session check
        return sessionToken != null && sessionToken.length() > 10;
    }

    /**
     * Part of ATTACK PATH 3C
     * Formats user ID (doesn't sanitize)
     */
    public String formatUserId(String userId) {
        // Simple formatting that preserves malicious input
        if (userId == null) {
            return "";
        }
        
        // Remove dashes (cosmetic, not security)
        return userId.replace("-", "");
    }
}
