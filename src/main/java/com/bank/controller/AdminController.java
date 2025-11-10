package com.bank.controller;

import com.bank.service.AccountService;
import com.bank.service.AdminService;
import com.bank.middleware.AuditMiddleware;
import com.bank.dto.AccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * ATTACK PATH SOURCE #2: Admin Controller
 * Creates ADDITIONAL attack paths to the SAME sinks in AccountRepository
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuditMiddleware auditMiddleware;

    /**
     * ATTACK PATH 2A → searchByNameUnsafe sink
     * Path: AdminController.adminSearchAccounts → AuditMiddleware.logAndSearch 
     *       → AdminService.performAdminSearch → AccountService.searchAccountsByName 
     *       → AccountRepository.searchByNameUnsafe
     * (5 hops to sink)
     */
    @GetMapping("/search-accounts")
    public List<AccountResponse> adminSearchAccounts(
            @RequestParam("query") String searchQuery,
            @RequestParam(value = "type", required = false) String accountType,
            HttpServletRequest request) {
        
        // Goes through audit middleware which adds another layer
        String adminUser = request.getHeader("X-Admin-User");
        return auditMiddleware.logAndSearch(adminUser, searchQuery, accountType);
    }

    /**
     * ATTACK PATH 2B → executeAuditQuery sink
     * Path: AdminController.viewAuditLogs → AdminService.retrieveAuditLogs
     *       → AuditMiddleware.filterAndQuery → AccountService.queryAuditLogsDirectly
     *       → AccountRepository.executeAuditQuery
     * (5 hops to sink)
     */
    @GetMapping("/audit-logs")
    public List<Map<String, Object>> viewAuditLogs(
            @RequestParam("userId") String userId,
            @RequestParam(value = "action", required = false) String action) {
        
        // Another path to the same audit query sink
        return adminService.retrieveAuditLogs(userId, action);
    }

    /**
     * ATTACK PATH 2C → searchByNameUnsafe sink (ALTERNATE PATH)
     * Path: AdminController.bulkAccountSearch → AdminService.searchMultipleAccounts
     *       → helper → AccountService.searchAccountsByName
     *       → AccountRepository.searchByNameUnsafe
     * (5 hops to sink)
     */
    @PostMapping("/bulk-search")
    public Map<String, List<AccountResponse>> bulkAccountSearch(
            @RequestBody Map<String, String> searchCriteria) {
        
        // Process multiple search queries that all flow to the same sink
        return adminService.searchMultipleAccounts(searchCriteria);
    }
}
