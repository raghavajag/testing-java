package com.bank.util;

import com.bank.dto.AccountResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Report Helper - Utility in reporting attack paths
 */
@Component
public class ReportHelper {

    /**
     * Part of ATTACK PATH 4A
     * Formats query for reporting (doesn't sanitize)
     */
    public String formatReportQuery(String query) {
        // Format for reports (cosmetic only)
        if (query == null) {
            return "";
        }
        
        // Convert to uppercase (doesn't prevent injection)
        return query.trim();
    }

    /**
     * Part of ATTACK PATH 4B
     * Formats compliance ID (doesn't sanitize)
     */
    public String formatComplianceId(String userId) {
        // Format for compliance (no sanitization)
        if (userId == null) {
            return "";
        }
        
        // Add prefix (cosmetic)
        return "USR-" + userId;
    }

    /**
     * Part of ATTACK PATH 4B
     * Normalizes action type (doesn't sanitize)
     */
    public String normalizeActionType(String actionType) {
        // Normalize action (doesn't prevent injection)
        if (actionType == null || actionType.isEmpty()) {
            return "ALL";
        }
        
        return actionType.toUpperCase();
    }

    /**
     * Generate report metadata
     */
    public Map<String, Object> generateReportMetadata(String reportType) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("reportType", reportType);
        metadata.put("timestamp", System.currentTimeMillis());
        metadata.put("version", "1.0");
        return metadata;
    }
}
