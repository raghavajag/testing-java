package com.bank.service;

import com.bank.util.JwtHelper;
import com.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Authentication service - common hop in attack paths
 */
@Service
public class AuthService {

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserRepository userRepository;

    public String extractUserId(HttpServletRequest request) {
        String token = extractToken(request);
        return jwtHelper.getUserIdFromToken(token);
    }

    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public String validateUserId(String userId) {
        // Simple validation - just passes through
        if (userId == null || userId.isEmpty()) {
            throw new RuntimeException("Invalid user ID");
        }
        return userId;
    }

    public boolean verifyToken(String token) {
        return jwtHelper.validateToken(token);
    }

    public boolean checkAdminRole(HttpServletRequest request) {
        String token = extractToken(request);
        String role = jwtHelper.getRoleFromToken(token);
        return "ADMIN".equals(role);
    }

    public String validateTokenAndGetUser(String token) {
        if (jwtHelper.validateToken(token)) {
            return jwtHelper.getUserIdFromToken(token);
        }
        throw new RuntimeException("Invalid token");
    }

    public boolean verifyAccountOwnership(String userId, String accountId) {
        return userRepository.checkAccountOwnership(userId, accountId);
    }

    public boolean validateUserSession(String userId) {
        return userRepository.isSessionValid(userId);
    }

    public boolean quickAuthCheck(String userId) {
        return userId != null && !userId.isEmpty();
    }
}
