package com.bank.controller;

import com.bank.service.LoanService;
import com.bank.service.AuthService;
import com.bank.service.AccountService;
import com.bank.dto.LoanApplicationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Loan management controller
 * Additional entry points for complex attack paths
 */
@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountService accountService;

    /**
     * VULN 5: SQL Injection via loan application
     * Complex path through multiple services
     */
    @PostMapping("/apply")
    public Map<String, Object> applyForLoan(
            @RequestBody LoanApplicationRequest loanRequest,
            HttpServletRequest request) {
        
        // Attack Path 6: Complex chain through validation and credit check (7 hops)
        String userId = authService.extractUserId(request);
        boolean isAuthenticated = authService.validateUserSession(userId);
        
        if (!isAuthenticated) {
            throw new RuntimeException("Not authenticated");
        }
        
        // Get user's primary account
        String primaryAccountId = accountService.getPrimaryAccountId(userId);
        
        // Check credit score
        int creditScore = loanService.getCreditScore(userId);
        
        // Calculate eligibility
        boolean isEligible = loanService.checkLoanEligibility(
            userId, loanRequest.getAmount(), creditScore);
        
        if (!isEligible) {
            return Map.of("status", "rejected", "reason", "Not eligible");
        }
        
        // Vulnerable: processes loan with user-controlled employer name
        String loanId = loanService.createLoanApplication(
            userId, 
            loanRequest.getAmount(),
            loanRequest.getEmployerName(), // User input flows here
            loanRequest.getIncome()
        );
        
        return Map.of("loanId", loanId, "status", "pending");
    }

    /**
     * Entry point with path to sanitized vulnerability
     */
    @GetMapping("/status")
    public Map<String, Object> getLoanStatus(
            @RequestParam("loanId") String loanId,
            HttpServletRequest request) {
        
        // Attack Path 7: Path to sanitized query (5 hops)
        String userId = authService.extractUserId(request);
        boolean authenticated = authService.quickAuthCheck(userId);
        
        if (!authenticated) {
            throw new RuntimeException("Unauthorized");
        }
        
        String validatedLoanId = loanService.validateLoanId(loanId);
        Map<String, Object> loanDetails = loanService.getLoanDetailsSafely(validatedLoanId);
        
        return loanDetails;
    }
}
