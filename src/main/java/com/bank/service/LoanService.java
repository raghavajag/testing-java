package com.bank.service;

import com.bank.repository.LoanRepository;
import com.bank.repository.CreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Loan service for complex attack paths
 */
@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private CreditRepository creditRepository;

    public int getCreditScore(String userId) {
        return creditRepository.fetchCreditScore(userId);
    }

    public boolean checkLoanEligibility(String userId, double amount, int creditScore) {
        // Business logic for loan eligibility
        if (creditScore < 600) {
            return false;
        }
        if (amount > 100000 && creditScore < 700) {
            return false;
        }
        return true;
    }

    /**
     * SINK METHOD 4: SQL injection via loan application
     * Employer name flows from user input
     */
    public String createLoanApplication(String userId, double amount, 
                                       String employerName, double income) {
        // Vulnerable: employerName goes to unsafe query
        String loanId = generateLoanId(userId, amount);
        loanRepository.insertLoanApplicationUnsafe(loanId, userId, amount, employerName, income);
        return loanId;
    }

    private String generateLoanId(String userId, double amount) {
        return "LOAN-" + userId + "-" + System.currentTimeMillis();
    }

    public String validateLoanId(String loanId) {
        // Basic validation but passes through
        if (loanId != null && loanId.startsWith("LOAN-")) {
            return loanId;
        }
        throw new RuntimeException("Invalid loan ID format");
    }

    /**
     * SAFE METHOD: Uses parameterized query
     */
    public Map<String, Object> getLoanDetailsSafely(String loanId) {
        // This one is safe
        return loanRepository.findLoanByIdSafe(loanId);
    }
}
