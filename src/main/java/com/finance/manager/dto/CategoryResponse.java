package com.finance.manager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.finance.manager.entity.CategoryType;

public class CategoryResponse {
    private Long id;
    private String name;
    private CategoryType type;
    private boolean isCustom;

    public CategoryResponse() {
    }

    public CategoryResponse(Long id, String name, CategoryType type, boolean isCustom) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isCustom = isCustom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    @JsonProperty("isCustom")
    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }
}
