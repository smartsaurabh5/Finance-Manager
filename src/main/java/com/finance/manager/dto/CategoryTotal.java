package com.finance.manager.dto;

import java.math.BigDecimal;

public class CategoryTotal {
    private Long categoryId;
    private String categoryName;
    private BigDecimal total;

    public CategoryTotal() {
    }

    public CategoryTotal(Long categoryId, String categoryName, BigDecimal total) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.total = total;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
