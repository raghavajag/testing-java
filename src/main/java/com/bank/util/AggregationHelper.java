package com.bank.util;

import com.bank.dto.AccountResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Aggregation Helper - Utility in reporting attack paths
 */
@Component
public class AggregationHelper {

    /**
     * Part of ATTACK PATH 4C
     * Pre-processes term (doesn't sanitize)
     */
    public String preprocessTerm(String searchTerm) {
        // Basic preprocessing (no security)
        if (searchTerm == null) {
            return "";
        }
        
        // Trim and normalize whitespace
        return searchTerm.trim().replaceAll("\\s+", " ");
    }

    /**
     * Part of ATTACK PATH 4C
     * Enhances with context (doesn't sanitize)
     */
    public String enhanceWithContext(String term) {
        // Add context (doesn't prevent injection)
        if (term == null || term.isEmpty()) {
            return "%";
        }
        
        // Just passes through the potentially malicious input
        return term;
    }

    /**
     * Part of ATTACK PATH 4C
     * Groups results by criteria
     */
    public Map<String, Object> groupResults(List<AccountResponse> accounts, String groupBy) {
        Map<String, Object> aggregated = new HashMap<>();
        
        if (groupBy == null || groupBy.isEmpty()) {
            aggregated.put("all", accounts);
            aggregated.put("total", accounts.size());
            return aggregated;
        }
        
        // Group by account type
        if ("type".equalsIgnoreCase(groupBy)) {
            Map<String, List<AccountResponse>> grouped = accounts.stream()
                .collect(Collectors.groupingBy(account -> 
                    account.getAccountType() != null ? account.getAccountType() : "unknown"));
            aggregated.putAll(grouped);
        } else {
            aggregated.put("all", accounts);
        }
        
        aggregated.put("total", accounts.size());
        return aggregated;
    }
}
