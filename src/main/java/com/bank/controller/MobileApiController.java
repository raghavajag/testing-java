package com.bank.controller;

import com.bank.service.MobileService;
import com.bank.middleware.CacheMiddleware;
import com.bank.dto.AccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * ATTACK PATH SOURCE #3: Mobile API Controller
 * Creates THIRD set of attack paths to the SAME sinks
 */
@RestController
@RequestMapping("/api/mobile/v1")
public class MobileApiController {

    @Autowired
    private MobileService mobileService;

    @Autowired
    private CacheMiddleware cacheMiddleware;

    /**
     * ATTACK PATH 3A → searchByNameUnsafe sink
     * Path: MobileApiController.mobileSearch → CacheMiddleware.cachedSearch
     *       → MobileService.searchAccountsForMobile → helper
     *       → AccountService.searchAccountsByName → AccountRepository.searchByNameUnsafe
     * (6 hops to sink)
     */
    @GetMapping("/search")
    public List<AccountResponse> mobileSearch(
            @RequestParam("q") String query,
            @RequestParam(value = "accountType", required = false) String accountType,
            @RequestHeader("X-Device-Id") String deviceId) {
        
        // Mobile path goes through caching layer
        return cacheMiddleware.cachedSearch(deviceId, query, accountType);
    }

    /**
     * ATTACK PATH 3B → searchByNameUnsafe sink (QUICK PATH)
     * Path: MobileApiController.quickSearch → MobileService.quickAccountSearch
     *       → AccountService.searchAccountsByName → AccountRepository.searchByNameUnsafe
     * (4 hops to sink)
     */
    @GetMapping("/quick-search")
    public List<AccountResponse> quickSearch(@RequestParam("name") String name) {
        // Shorter path to the same sink
        return mobileService.quickAccountSearch(name);
    }

    /**
     * ATTACK PATH 3C → executeAuditQuery sink
     * Path: MobileApiController.getMobileAuditTrail → MobileService.fetchUserAuditTrail
     *       → helper → AccountService.queryAuditLogsDirectly
     *       → AccountRepository.executeAuditQuery
     * (5 hops to sink)
     */
    @GetMapping("/audit-trail")
    public List<Map<String, Object>> getMobileAuditTrail(
            @RequestParam("userId") String userId,
            @RequestHeader("X-Session-Token") String sessionToken) {
        
        return mobileService.fetchUserAuditTrail(userId, sessionToken);
    }
}
