package com.bank.middleware;

import com.bank.service.ReportService;
import com.bank.dto.AccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validation Middleware - Adds validation layer in attack paths
 */
@Component
public class ValidationMiddleware {

    @Autowired
    private ReportService reportService;

    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("[a-zA-Z0-9 ]+");

    /**
     * Part of ATTACK PATH 4A
     * Called by: ReportController.generateAccountReport
     * Calls: ReportService.collectAccountData
     */
    public Map<String, Object> validateAndGenerateReport(String query, String accountType) {
        // Validate input format (but doesn't prevent SQL injection)
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("Query cannot be empty");
        }
        
        // Basic format check (ineffective against SQL injection)
        if (query.length() > 1000) {
            throw new IllegalArgumentException("Query too long");
        }
        
        // Delegate to report service which flows to sink
        return reportService.collectAccountData(query, accountType);
    }

    /**
     * Ineffective validation - allows malicious input through
     */
    private boolean isValidInput(String input) {
        // This validation is too permissive and won't catch SQL injection
        return input != null && !input.isEmpty();
    }
}
