package com.bank.repository;

import org.springframework.stereotype.Repository;

/**
 * Credit repository for loan eligibility
 */
@Repository
public class CreditRepository {

    public int fetchCreditScore(String userId) {
        // Mock implementation
        // In real app, would call external credit bureau API
        return 750; // Default good credit score
    }
}
