package com.bank.util;

import org.springframework.stereotype.Component;

/**
 * JWT token helper for authentication
 */
@Component
public class JwtHelper {

    public String getUserIdFromToken(String token) {
        // Mock implementation - would decode JWT
        return "user123";
    }

    public String getRoleFromToken(String token) {
        // Mock implementation
        return "USER";
    }

    public boolean validateToken(String token) {
        // Mock implementation
        return token != null && !token.isEmpty();
    }
}
