package com.bank.service;

import com.bank.repository.AccountRepository;
import com.bank.dto.AccountResponse;
import com.bank.util.SearchHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Admin Service - Intermediary in attack paths from AdminController
 */
@Service
public class AdminService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private SearchHelper searchHelper;

    /**
     * Part of ATTACK PATH 2A
     * Called by: AuditMiddleware.logAndSearch
     * Calls: AccountService.searchAccountsByName
     */
    public List<AccountResponse> performAdminSearch(String query, String accountType) {
        // Pre-process the search query
        String processedQuery = searchHelper.preprocessSearchQuery(query);
        
        // Flows to AccountService which flows to the sink
        return accountService.searchAccountsByName(processedQuery, accountType);
    }

    /**
     * Part of ATTACK PATH 2B
     * Called by: AdminController.viewAuditLogs
     * Calls: AuditMiddleware.filterAndQuery
     */
    public List<Map<String, Object>> retrieveAuditLogs(String userId, String action) {
        // Validate admin access (simplified)
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID required");
        }
        
        // Flows through audit middleware to service to sink
        return accountService.queryAuditLogsDirectly(userId, action);
    }

    /**
     * Part of ATTACK PATH 2C
     * Called by: AdminController.bulkAccountSearch
     * Calls: AccountService.searchAccountsByName (multiple times)
     */
    public Map<String, List<AccountResponse>> searchMultipleAccounts(
            Map<String, String> searchCriteria) {
        
        Map<String, List<AccountResponse>> results = new HashMap<>();
        
        // Each search query flows to the same sink
        for (Map.Entry<String, String> entry : searchCriteria.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            // Process through helper
            String enrichedValue = searchHelper.enrichSearchTerm(value);
            
            // Each call creates a path to the sink
            List<AccountResponse> accounts = accountService.searchAccountsByName(
                enrichedValue, null);
            results.put(key, accounts);
        }
        
        return results;
    }
}
