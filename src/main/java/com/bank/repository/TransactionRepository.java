package com.bank.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Transaction repository with safe implementations
 */
@Repository
public class TransactionRepository {

    @Autowired
    private DataSource dataSource;

    /**
     * SAFE METHOD: Uses parameterized queries
     */
    public List<Map<String, Object>> findTransactionsSafely(String accountId, String startDate) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        String query = "SELECT * FROM transactions WHERE account_id = ? AND transaction_date >= ? ORDER BY transaction_date DESC";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, accountId);
            stmt.setString(2, startDate);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("transactionId", rs.getString("transaction_id"));
                transaction.put("amount", rs.getDouble("amount"));
                transaction.put("date", rs.getTimestamp("transaction_date"));
                transaction.put("type", rs.getString("transaction_type"));
                results.add(transaction);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Transaction query failed", e);
        }
        
        return results;
    }

    public void createTransaction(String fromAccountId, String toAccountId, double amount) {
        String query = "INSERT INTO transactions (from_account, to_account, amount, transaction_date) VALUES (?, ?, ?, NOW())";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, fromAccountId);
            stmt.setString(2, toAccountId);
            stmt.setDouble(3, amount);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Transaction creation failed", e);
        }
    }

    public double sumPendingTransactions(String accountId) {
        String query = "SELECT SUM(amount) as total FROM transactions WHERE account_id = ? AND status = 'PENDING'";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, accountId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Sum query failed", e);
        }
        
        return 0.0;
    }
}
