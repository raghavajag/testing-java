package com.bank.controller;

import com.bank.service.ReportService;
import com.bank.middleware.ValidationMiddleware;
import com.bank.dto.AccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ATTACK PATH SOURCE #4: Reporting Controller
 * Creates FOURTH set of attack paths to the SAME sinks
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ValidationMiddleware validationMiddleware;

    /**
     * ATTACK PATH 4A → searchByNameUnsafe sink
     * Path: ReportController.generateAccountReport → ValidationMiddleware.validateAndSearch
     *       → ReportService.collectAccountData → helper
     *       → AccountService.searchAccountsByName → AccountRepository.searchByNameUnsafe
     * (6 hops to sink)
     */
    @PostMapping("/account-summary")
    public Map<String, Object> generateAccountReport(
            @RequestBody Map<String, String> reportCriteria) {
        
        String accountQuery = reportCriteria.get("accountQuery");
        String accountType = reportCriteria.get("accountType");
        
        // Goes through validation middleware
        return validationMiddleware.validateAndGenerateReport(accountQuery, accountType);
    }

    /**
     * ATTACK PATH 4B → executeAuditQuery sink
     * Path: ReportController.complianceReport → ReportService.generateComplianceReport
     *       → helper → AccountService.queryAuditLogsDirectly
     *       → AccountRepository.executeAuditQuery
     * (5 hops to sink)
     */
    @GetMapping("/compliance")
    public List<Map<String, Object>> complianceReport(
            @RequestParam("userId") String userId,
            @RequestParam("actionType") String actionType) {
        
        return reportService.generateComplianceReport(userId, actionType);
    }

    /**
     * ATTACK PATH 4C → searchByNameUnsafe sink (via aggregation)
     * Path: ReportController.aggregateAccountData → ReportService.aggregateAccounts
     *       → multiple helpers → AccountService.searchAccountsByName
     *       → AccountRepository.searchByNameUnsafe
     * (5 hops to sink)
     */
    @GetMapping("/aggregate")
    public Map<String, Object> aggregateAccountData(
            @RequestParam("searchTerm") String searchTerm,
            @RequestParam(value = "groupBy", required = false) String groupBy) {
        
        return reportService.aggregateAccounts(searchTerm, groupBy);
    }
}
