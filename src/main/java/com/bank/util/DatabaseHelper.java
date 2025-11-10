package com.bank.util;

import org.springframework.stereotype.Component;

/**
 * Database utility class
 */
@Component
public class DatabaseHelper {

    public String escapeSQL(String input) {
        // Basic SQL escaping
        if (input == null) {
            return "";
        }
        return input.replace("'", "''");
    }

    public boolean isValidInput(String input) {
        // Basic validation
        return input != null && !input.contains("--") && !input.contains(";");
    }
}
