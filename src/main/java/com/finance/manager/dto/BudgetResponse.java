package com.finance.manager.dto;

import java.math.BigDecimal;

public class BudgetResponse {
    private Long id;
    private int year;
    private int month;
    private Long categoryId;
    private String categoryName;
    private BigDecimal limitAmount;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;

    public BudgetResponse(Long id, int year, int month, Long categoryId, String categoryName,
                          BigDecimal limitAmount, BigDecimal spentAmount, BigDecimal remainingAmount) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.limitAmount = limitAmount;
        this.spentAmount = spentAmount;
        this.remainingAmount = remainingAmount;
    }

    public Long getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }
}
