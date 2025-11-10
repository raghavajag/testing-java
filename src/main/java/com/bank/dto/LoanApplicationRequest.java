package com.bank.dto;

/**
 * Loan application request DTO
 */
public class LoanApplicationRequest {
    private double amount;
    private String employerName;
    private double income;
    private int termMonths;

    // Getters and Setters
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getEmployerName() { return employerName; }
    public void setEmployerName(String employerName) { this.employerName = employerName; }

    public double getIncome() { return income; }
    public void setIncome(double income) { this.income = income; }

    public int getTermMonths() { return termMonths; }
    public void setTermMonths(int termMonths) { this.termMonths = termMonths; }
}
