package com.finance.manager.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardSummaryResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netSavings;
    private List<TransactionResponse> recentTransactions;

    public DashboardSummaryResponse(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal netSavings,
                                    List<TransactionResponse> recentTransactions) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.netSavings = netSavings;
        this.recentTransactions = recentTransactions;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public BigDecimal getNetSavings() {
        return netSavings;
    }

    public List<TransactionResponse> getRecentTransactions() {
        return recentTransactions;
    }
}
