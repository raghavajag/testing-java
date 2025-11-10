package com.bank.controller;

import com.bank.service.AccountService;
import com.bank.service.AuthService;
import com.bank.service.ValidationService;
import com.bank.dto.TransferRequest;
import com.bank.dto.AccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Main REST controller for banking operations
 * Entry points for attack path analysis
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ValidationService validationService;

    /**
     * VULN 1: SQL Injection - CRITICAL (TRUE POSITIVE)
     * Multiple attack paths from this entry point
     */
    @GetMapping("/search")
    public List<AccountResponse> searchAccounts(
            @RequestParam("query") String query,
            @RequestParam(value = "type", required = false) String accountType,
            HttpServletRequest request) {
        
        // Attack Path 1: Direct search (5 hops)
        String userId = authService.extractUserId(request);
        String sanitizedUserId = authService.validateUserId(userId);
        AccountResponse userAccount = accountService.findUserAccount(sanitizedUserId);
        List<AccountResponse> results = accountService.searchAccountsByName(query, accountType);
        return results;
    }

    /**
     * VULN 2: SQL Injection - But SANITIZED (FALSE POSITIVE)
     * Attack paths exist but sanitization should prevent exploitation
     */
    @GetMapping("/transactions")
    public List<Map<String, Object>> getTransactionHistory(
            @RequestParam("accountId") String accountId,
            @RequestParam("startDate") String startDate,
            HttpServletRequest request) {
        
        // Attack Path 2: Via validation chain (6 hops)
        String token = authService.extractToken(request);
        boolean isValid = authService.verifyToken(token);
        
        if (!isValid) {
            throw new RuntimeException("Invalid token");
        }
        
        String sanitizedAccountId = validationService.sanitizeInput(accountId);
        String sanitizedDate = validationService.validateDate(startDate);
        List<Map<String, Object>> transactions = accountService.getTransactionHistorySafe(
            sanitizedAccountId, sanitizedDate);
        return transactions;
    }

    /**
     * VULN 3: SQL Injection - ADMIN ONLY (FALSE POSITIVE - PROTECTED)
     * Protected by authentication and authorization
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/audit")
    public List<Map<String, Object>> getAuditLogs(
            @RequestParam("userId") String userId,
            @RequestParam(value = "action", required = false) String action,
            HttpServletRequest request) {
        
        // Attack Path 3: Via admin validation chain (5 hops)
        boolean isAdmin = authService.checkAdminRole(request);
        
        if (!isAdmin) {
            throw new RuntimeException("Unauthorized");
        }
        
        String normalizedUserId = validationService.normalizeUserId(userId);
        String verifiedAction = validationService.verifyActionType(action);
        List<Map<String, Object>> logs = accountService.queryAuditLogsDirectly(
            normalizedUserId, verifiedAction);
        return logs;
    }

    /**
     * VULN 4: Template Injection - CRITICAL (TRUE POSITIVE)
     * Multiple attack paths through notification system
     */
    @PostMapping("/transfer")
    public Map<String, Object> transferFunds(
            @RequestBody TransferRequest transferRequest,
            HttpServletRequest request) {
        
        // Attack Path 4: Via notification chain (6 hops)
        String userId = authService.extractUserId(request);
        AccountResponse fromAccount = accountService.getAccountById(transferRequest.getFromAccountId());
        AccountResponse toAccount = accountService.findAccountByNumber(transferRequest.getToAccountNumber());
        
        boolean hasBalance = accountService.checkSufficientBalance(
            fromAccount.getAccountId(), transferRequest.getAmount());
        
        if (!hasBalance) {
            return Map.of("error", "Insufficient funds");
        }
        
        String transactionId = accountService.executeTransfer(
            fromAccount, toAccount, transferRequest.getAmount());
        
        // Vulnerable: generates email notification with template injection
        accountService.sendTransferNotification(
            fromAccount, toAccount, transferRequest.getAmount(), 
            transferRequest.getCustomMessage());
        
        return Map.of("transactionId", transactionId, "status", "success");
    }

    /**
     * DEAD CODE: This endpoint is never called
     * Should be classified as false positive (dead code)
     */
    @GetMapping("/legacy/old-search")
    public List<AccountResponse> oldSearchMethod(@RequestParam("name") String name) {
        // This is old code that's no longer used
        return accountService.legacySearchUnsafe(name);
    }

    /**
     * Another entry point with multiple paths
     */
    @GetMapping("/balance")
    public Map<String, Object> getBalance(
            @RequestParam("accountId") String accountId,
            HttpServletRequest request) {
        
        // Attack Path 5: Via balance check chain (5 hops)
        String token = authService.extractToken(request);
        String userId = authService.validateTokenAndGetUser(token);
        boolean ownsAccount = authService.verifyAccountOwnership(userId, accountId);
        
        if (!ownsAccount) {
            throw new RuntimeException("Unauthorized access");
        }
        
        AccountResponse account = accountService.getAccountDetailsForBalance(accountId);
        double balance = accountService.calculateCurrentBalance(account);
        
        return Map.of(
            "accountId", accountId,
            "balance", balance,
            "currency", account.getCurrency()
        );
    }
}
