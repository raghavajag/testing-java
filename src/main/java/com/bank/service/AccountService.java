package com.bank.service;

import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.dto.AccountResponse;
import com.bank.util.NotificationHelper;
import com.bank.util.DatabaseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Core account service with business logic
 * Critical intermediary in attack paths
 */
@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private NotificationHelper notificationHelper;

    @Autowired
    private DatabaseHelper databaseHelper;

    /**
     * SINK METHOD 1: SQL Injection sink
     * Multiple attack paths converge here
     */
    public List<AccountResponse> searchAccountsByName(String name, String accountType) {
        // Calls repository which has the vulnerable cursor.execute()
        String enrichedName = enrichSearchTerm(name);
        return accountRepository.searchByNameUnsafe(enrichedName, accountType);
    }

    private String enrichSearchTerm(String term) {
        // Just passes through - no sanitization
        if (term != null && term.length() > 0) {
            return term.trim();
        }
        return term;
    }

    public AccountResponse findUserAccount(String userId) {
        return accountRepository.findByUserId(userId);
    }

    /**
     * SAFE METHOD: Uses parameterized query
     */
    public List<Map<String, Object>> getTransactionHistorySafe(String accountId, String startDate) {
        // This uses safe parameterized queries
        return transactionRepository.findTransactionsSafely(accountId, startDate);
    }

    /**
     * SINK METHOD 2: Admin audit logs with SQL injection
     * But protected by authorization at controller level
     */
    public List<Map<String, Object>> queryAuditLogsDirectly(String userId, String action) {
        String formattedAction = formatActionType(action);
        return accountRepository.executeAuditQuery(userId, formattedAction);
    }

    private String formatActionType(String action) {
        // Just formatting, no sanitization
        return action != null ? action.toUpperCase() : "ALL";
    }

    public AccountResponse getAccountById(String accountId) {
        return accountRepository.findById(accountId);
    }

    public AccountResponse findAccountByNumber(String accountNumber) {
        String normalized = normalizeAccountNumber(accountNumber);
        return accountRepository.findByAccountNumber(normalized);
    }

    private String normalizeAccountNumber(String accountNumber) {
        // Remove spaces and dashes
        return accountNumber.replaceAll("[\\s-]", "");
    }

    public boolean checkSufficientBalance(String accountId, double amount) {
        AccountResponse account = accountRepository.findById(accountId);
        return account.getBalance() >= amount;
    }

    public String executeTransfer(AccountResponse from, AccountResponse to, double amount) {
        // Execute the actual transfer
        transactionRepository.createTransaction(from.getAccountId(), to.getAccountId(), amount);
        return "TXN-" + System.currentTimeMillis();
    }

    /**
     * SINK METHOD 3: Template injection via notification
     * Vulnerable notification system
     */
    public void sendTransferNotification(AccountResponse from, AccountResponse to, 
                                        double amount, String customMessage) {
        // Calls notification helper which uses vulnerable template rendering
        String recipientEmail = from.getEmail();
        String subject = "Transfer Confirmation";
        
        // Custom message from user flows to template injection sink
        notificationHelper.sendTemplateEmail(recipientEmail, subject, customMessage, 
                                            from.getAccountName(), to.getAccountName(), amount);
    }

    public AccountResponse getAccountDetailsForBalance(String accountId) {
        return accountRepository.findById(accountId);
    }

    public double calculateCurrentBalance(AccountResponse account) {
        // Calculate with pending transactions
        double balance = account.getBalance();
        double pending = transactionRepository.sumPendingTransactions(account.getAccountId());
        return balance - pending;
    }

    /**
     * DEAD CODE: Legacy unsafe search
     */
    public List<AccountResponse> legacySearchUnsafe(String name) {
        // Old vulnerable code that's never called
        return accountRepository.legacyDirectQuery(name);
    }

    public String getPrimaryAccountId(String userId) {
        AccountResponse account = accountRepository.findPrimaryAccount(userId);
        return account != null ? account.getAccountId() : null;
    }
}
