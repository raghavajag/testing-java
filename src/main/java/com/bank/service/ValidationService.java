package com.bank.service;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

/**
 * Validation service for sanitization paths
 */
@Service
public class ValidationService {

    private static final Pattern SQL_PATTERN = Pattern.compile("[';\"\\-\\-]");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    /**
     * SANITIZATION METHOD: Removes SQL injection characters
     */
    public String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        // Remove dangerous SQL characters
        return SQL_PATTERN.matcher(input).replaceAll("");
    }

    public String validateDate(String date) {
        if (date == null || !DATE_PATTERN.matcher(date).matches()) {
            return "2024-01-01"; // Default safe date
        }
        return date;
    }

    public String normalizeUserId(String userId) {
        // Just passes through with basic check
        return userId != null ? userId.trim() : "";
    }

    public String verifyActionType(String action) {
        // Whitelist check
        if (action == null) {
            return "VIEW";
        }
        String upper = action.toUpperCase();
        if (upper.equals("VIEW") || upper.equals("CREATE") || upper.equals("UPDATE")) {
            return upper;
        }
        return "VIEW";
    }
}
