package com.bank.controller;

import com.bank.service.AccountService;
import com.bank.dto.AccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ATTACK PATH SOURCE #1: Main Account Controller
 * Simple entry points that flow to sinks via AccountService
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * ATTACK PATH 1A → searchByNameUnsafe sink
     * Path: AccountController.searchAccounts → AccountService.searchAccountsByName 
     *       → AccountRepository.searchByNameUnsafe
     * (3 hops - BASIC PATH)
     */
    @GetMapping("/search")
    public List<AccountResponse> searchAccounts(
            @RequestParam("query") String query,
            @RequestParam(value = "type", required = false) String accountType) {
        
        // Simple direct path to service
        return accountService.searchAccountsByName(query, accountType);
    }

    /**
     * ATTACK PATH 1B → executeAuditQuery sink
     * Path: AccountController.getSimpleAuditLogs → AccountService.queryAuditLogsDirectly
     *       → AccountRepository.executeAuditQuery
     * (3 hops - BASIC PATH)
     */
    @GetMapping("/audit")
    public List<Map<String, Object>> getSimpleAuditLogs(
            @RequestParam("userId") String userId,
            @RequestParam(value = "action", required = false) String action) {
        
        // Simple direct path to service
        return accountService.queryAuditLogsDirectly(userId, action);
    }
}

