package com.bank.middleware;

import com.bank.service.MobileService;
import com.bank.dto.AccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache Middleware - Adds caching layer in attack paths
 */
@Component
public class CacheMiddleware {

    @Autowired
    private MobileService mobileService;

    private Map<String, List<AccountResponse>> cache = new HashMap<>();

    /**
     * Part of ATTACK PATH 3A
     * Called by: MobileApiController.mobileSearch
     * Calls: MobileService.searchAccountsForMobile
     */
    public List<AccountResponse> cachedSearch(String deviceId, String query, String accountType) {
        // Check cache
        String cacheKey = generateCacheKey(deviceId, query, accountType);
        
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }
        
        // Cache miss - delegate to mobile service which flows to sink
        List<AccountResponse> results = mobileService.searchAccountsForMobile(query, accountType);
        
        // Store in cache
        cache.put(cacheKey, results);
        
        return results;
    }

    private String generateCacheKey(String deviceId, String query, String accountType) {
        return deviceId + ":" + query + ":" + (accountType != null ? accountType : "all");
    }

    public void clearCache() {
        cache.clear();
    }
}
