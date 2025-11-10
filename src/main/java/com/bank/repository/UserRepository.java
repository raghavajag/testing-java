package com.bank.repository;

import org.springframework.stereotype.Repository;

/**
 * User repository for authentication checks
 */
@Repository
public class UserRepository {

    public boolean checkAccountOwnership(String userId, String accountId) {
        // Mock implementation - would check database
        return true;
    }

    public boolean isSessionValid(String userId) {
        // Mock implementation - would check session store
        return userId != null && !userId.isEmpty();
    }
}
