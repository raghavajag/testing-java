package com.bank.service;

import com.bank.dto.AccountResponse;
import com.bank.util.MobileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Mobile Service - Intermediary in attack paths from MobileApiController
 */
@Service
public class MobileService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MobileHelper mobileHelper;

    /**
     * Part of ATTACK PATH 3A
     * Called by: CacheMiddleware.cachedSearch
     * Calls: helper → AccountService.searchAccountsByName
     */
    public List<AccountResponse> searchAccountsForMobile(String query, String accountType) {
        // Format for mobile display
        String mobileFormattedQuery = mobileHelper.formatForMobile(query);
        
        // Another layer before reaching AccountService
        String optimizedQuery = mobileHelper.optimizeSearchQuery(mobileFormattedQuery);
        
        // Flows to AccountService which flows to the sink
        return accountService.searchAccountsByName(optimizedQuery, accountType);
    }

    /**
     * Part of ATTACK PATH 3B (shorter path)
     * Called by: MobileApiController.quickSearch
     * Calls: AccountService.searchAccountsByName
     */
    public List<AccountResponse> quickAccountSearch(String name) {
        // Direct path with minimal processing
        return accountService.searchAccountsByName(name, null);
    }

    /**
     * Part of ATTACK PATH 3C
     * Called by: MobileApiController.getMobileAuditTrail
     * Calls: helper → AccountService.queryAuditLogsDirectly
     */
    public List<Map<String, Object>> fetchUserAuditTrail(String userId, String sessionToken) {
        // Verify session
        boolean isValid = mobileHelper.verifySession(sessionToken);
        
        if (!isValid) {
            throw new SecurityException("Invalid session");
        }
        
        // Format user ID for mobile
        String formattedUserId = mobileHelper.formatUserId(userId);
        
        // Flows to AccountService which flows to audit sink
        return accountService.queryAuditLogsDirectly(formattedUserId, "MOBILE_ACCESS");
    }
}
