package com.finance.manager.dto;

import java.math.BigDecimal;
import java.util.List;

public class ReportResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;
    private List<CategoryTotal> expensesByCategory;
    private List<CategoryTotal> incomeByCategory;

    public ReportResponse() {
    }

    public ReportResponse(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal netBalance, List<CategoryTotal> expensesByCategory, List<CategoryTotal> incomeByCategory) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.netBalance = netBalance;
        this.expensesByCategory = expensesByCategory;
        this.incomeByCategory = incomeByCategory;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public BigDecimal getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(BigDecimal netBalance) {
        this.netBalance = netBalance;
    }

    public List<CategoryTotal> getExpensesByCategory() {
        return expensesByCategory;
    }

    public void setExpensesByCategory(List<CategoryTotal> expensesByCategory) {
        this.expensesByCategory = expensesByCategory;
    }

    public List<CategoryTotal> getIncomeByCategory() {
        return incomeByCategory;
    }

    public void setIncomeByCategory(List<CategoryTotal> incomeByCategory) {
        this.incomeByCategory = incomeByCategory;
    }
}
