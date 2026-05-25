package com.finance.manager.dto;

import com.finance.manager.entity.CategoryType;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private LocalDate date;
    private String category;
    private String description;
    private CategoryType type;

    public TransactionResponse() {
    }

    public TransactionResponse(Long id, BigDecimal amount, LocalDate date, String category, String description, CategoryType type) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.description = description;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public CategoryType getType() {
        return type;
    }
}
