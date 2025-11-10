package com.bank.service;

import com.bank.dto.AccountResponse;
import com.bank.util.ReportHelper;
import com.bank.util.AggregationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Report Service - Intermediary in attack paths from ReportController
 */
@Service
public class ReportService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ReportHelper reportHelper;

    @Autowired
    private AggregationHelper aggregationHelper;

    /**
     * Part of ATTACK PATH 4A
     * Called by: ValidationMiddleware.validateAndGenerateReport
     * Calls: helper → AccountService.searchAccountsByName
     */
    public Map<String, Object> collectAccountData(String query, String accountType) {
        // Generate report metadata
        Map<String, Object> report = new HashMap<>();
        report.put("timestamp", System.currentTimeMillis());
        report.put("reportType", "account_summary");
        
        // Format query for reporting
        String reportQuery = reportHelper.formatReportQuery(query);
        
        // Search accounts - flows to sink
        List<AccountResponse> accounts = accountService.searchAccountsByName(
            reportQuery, accountType);
        
        report.put("accounts", accounts);
        report.put("count", accounts.size());
        
        return report;
    }

    /**
     * Part of ATTACK PATH 4B
     * Called by: ReportController.complianceReport
     * Calls: helper → AccountService.queryAuditLogsDirectly
     */
    public List<Map<String, Object>> generateComplianceReport(String userId, String actionType) {
        // Format for compliance reporting
        String complianceUserId = reportHelper.formatComplianceId(userId);
        String complianceAction = reportHelper.normalizeActionType(actionType);
        
        // Query audit logs - flows to sink
        return accountService.queryAuditLogsDirectly(complianceUserId, complianceAction);
    }

    /**
     * Part of ATTACK PATH 4C
     * Called by: ReportController.aggregateAccountData
     * Calls: multiple helpers → AccountService.searchAccountsByName
     */
    public Map<String, Object> aggregateAccounts(String searchTerm, String groupBy) {
        // Pre-process search term
        String processedTerm = aggregationHelper.preprocessTerm(searchTerm);
        
        // Enhance with aggregation context
        String enhancedTerm = aggregationHelper.enhanceWithContext(processedTerm);
        
        // Search accounts - flows to sink
        List<AccountResponse> accounts = accountService.searchAccountsByName(
            enhancedTerm, null);
        
        // Group results
        Map<String, Object> aggregated = aggregationHelper.groupResults(accounts, groupBy);
        
        return aggregated;
    }
}
