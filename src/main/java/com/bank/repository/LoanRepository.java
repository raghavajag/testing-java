package com.bank.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Loan repository with vulnerable and safe methods
 */
@Repository
public class LoanRepository {

    @Autowired
    private DataSource dataSource;

    /**
     * CRITICAL SINK: SQL injection via employer name
     */
    public void insertLoanApplicationUnsafe(String loanId, String userId, double amount, 
                                           String employerName, double income) {
        // Vulnerable: Direct string concatenation
        String query = "INSERT INTO loan_applications (loan_id, user_id, amount, employer_name, income, status) " +
                      "VALUES ('" + loanId + "', '" + userId + "', " + amount + ", '" + 
                      employerName + "', " + income + ", 'PENDING')";
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // SINK: Vulnerable execute
            stmt.executeUpdate(query);
            
        } catch (SQLException e) {
            throw new RuntimeException("Loan application failed", e);
        }
    }

    /**
     * SAFE METHOD: Parameterized query
     */
    public Map<String, Object> findLoanByIdSafe(String loanId) {
        String query = "SELECT * FROM loan_applications WHERE loan_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, loanId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Map<String, Object> loan = new HashMap<>();
                loan.put("loanId", rs.getString("loan_id"));
                loan.put("amount", rs.getDouble("amount"));
                loan.put("status", rs.getString("status"));
                loan.put("employerName", rs.getString("employer_name"));
                return loan;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loan query failed", e);
        }
        
        return null;
    }
}
