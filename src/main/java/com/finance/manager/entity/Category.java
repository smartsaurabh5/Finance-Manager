package com.finance.manager.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private CategoryType type;

    private Boolean isDefault = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Category() {
    }

    public Category(String name, CategoryType type, Boolean isDefault, User user) {
        this.name = name;
        this.type = type;
        this.isDefault = isDefault;
        this.user = user;
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

    public Boolean getIsDefault() {
        return isDefault;
    }

    public boolean isDefault() {
        return Boolean.TRUE.equals(isDefault);
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static Category.CategoryBuilder builder() {
        return new Category.CategoryBuilder();
    }

    public static class CategoryBuilder {
        private Long id;
        private String name;
        private CategoryType type;
        private Boolean isDefault;
        private User user;

        CategoryBuilder() {
        }

        public Category.CategoryBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        public Category.CategoryBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public Category.CategoryBuilder type(final CategoryType type) {
            this.type = type;
            return this;
        }

        public Category.CategoryBuilder isDefault(final Boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public Category.CategoryBuilder user(final User user) {
            this.user = user;
            return this;
        }

        public Category build() {
            return new Category(this.id, this.name, this.type, this.isDefault, this.user);
        }
    }

    public Category(Long id, String name, CategoryType type, Boolean isDefault, User user) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isDefault = isDefault;
        this.user = user;
    }
}
