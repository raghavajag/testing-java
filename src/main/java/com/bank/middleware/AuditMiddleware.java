package com.bank.middleware;

import com.bank.service.AdminService;
import com.bank.service.AccountService;
import com.bank.dto.AccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Audit Middleware - Adds logging layer in attack paths
 */
@Component
public class AuditMiddleware {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AccountService accountService;

    /**
     * Part of ATTACK PATH 2A
     * Called by: AdminController.adminSearchAccounts
     * Calls: AdminService.performAdminSearch
     */
    public List<AccountResponse> logAndSearch(String adminUser, String query, String accountType) {
        // Log the admin search operation
        logAdminOperation(adminUser, "SEARCH", query);
        
        // Delegate to admin service which flows to sink
        return adminService.performAdminSearch(query, accountType);
    }

    /**
     * Part of ATTACK PATH 2B (alternate)
     * Called by: AdminService.retrieveAuditLogs
     * Calls: AccountService.queryAuditLogsDirectly
     */
    public List<Map<String, Object>> filterAndQuery(String userId, String action) {
        // Apply filters
        String filteredAction = applyAuditFilter(action);
        
        // Query audit logs - flows to sink
        return accountService.queryAuditLogsDirectly(userId, filteredAction);
    }

    private void logAdminOperation(String adminUser, String operation, String details) {
        // Simplified logging
        System.out.println("[AUDIT] Admin: " + adminUser + " Operation: " + operation);
    }

    private String applyAuditFilter(String action) {
        // Apply business logic filters
        if (action == null || action.isEmpty()) {
            return "ALL";
        }
        return action.toUpperCase();
    }
}
