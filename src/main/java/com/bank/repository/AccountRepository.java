package com.bank.repository;

import com.bank.dto.AccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository layer with actual SQL injection sinks
 */
@Repository
public class AccountRepository {

    @Autowired
    private DataSource dataSource;

    /**
     * CRITICAL SINK: SQL Injection vulnerability
     * This is where cursor.execute() happens with unsanitized input
     */
    public List<AccountResponse> searchByNameUnsafe(String name, String accountType) {
        List<AccountResponse> results = new ArrayList<>();
        String query;
        
        if (accountType != null && !accountType.isEmpty()) {
            // Vulnerable: String concatenation in SQL
            query = "SELECT * FROM accounts WHERE account_name LIKE '%" + name + 
                   "%' AND account_type = '" + accountType + "'";
        } else {
            query = "SELECT * FROM accounts WHERE account_name LIKE '%" + name + "%'";
        }
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // SINK: Vulnerable cursor.execute() equivalent
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                AccountResponse account = mapResultToAccount(rs);
                results.add(account);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
        
        return results;
    }

    /**
     * CRITICAL SINK: Admin audit query with SQL injection
     */
    public List<Map<String, Object>> executeAuditQuery(String userId, String action) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        // Vulnerable: Direct string concatenation
        String query = "SELECT * FROM audit_logs WHERE user_id = '" + userId + 
                      "' AND action = '" + action + "' ORDER BY timestamp DESC";
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // SINK: Vulnerable execution
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                Map<String, Object> log = new HashMap<>();
                log.put("timestamp", rs.getTimestamp("timestamp"));
                log.put("action", rs.getString("action"));
                log.put("details", rs.getString("details"));
                results.add(log);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Audit query failed", e);
        }
        
        return results;
    }

    /**
     * DEAD CODE SINK: Never called
     */
    public List<AccountResponse> legacyDirectQuery(String name) {
        String query = "SELECT * FROM accounts WHERE name = '" + name + "'";
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            List<AccountResponse> results = new ArrayList<>();
            while (rs.next()) {
                results.add(mapResultToAccount(rs));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Legacy query failed", e);
        }
    }

    // Safe methods using prepared statements
    
    public AccountResponse findByUserId(String userId) {
        String query = "SELECT * FROM accounts WHERE user_id = ? AND is_primary = true";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultToAccount(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
        
        return null;
    }

    public AccountResponse findById(String accountId) {
        String query = "SELECT * FROM accounts WHERE account_id = ?";
        return executeQueryForSingleAccount(query, accountId);
    }

    public AccountResponse findByAccountNumber(String accountNumber) {
        String query = "SELECT * FROM accounts WHERE account_number = ?";
        return executeQueryForSingleAccount(query, accountNumber);
    }

    public AccountResponse findPrimaryAccount(String userId) {
        String query = "SELECT * FROM accounts WHERE user_id = ? AND is_primary = true";
        return executeQueryForSingleAccount(query, userId);
    }

    private AccountResponse executeQueryForSingleAccount(String query, String param) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, param);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultToAccount(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
        
        return null;
    }

    private AccountResponse mapResultToAccount(ResultSet rs) throws SQLException {
        AccountResponse account = new AccountResponse();
        account.setAccountId(rs.getString("account_id"));
        account.setAccountName(rs.getString("account_name"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setBalance(rs.getDouble("balance"));
        account.setCurrency(rs.getString("currency"));
        account.setEmail(rs.getString("email"));
        return account;
    }
}
