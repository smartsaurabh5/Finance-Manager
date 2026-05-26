package com.finance.manager.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 255)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private UserRole role = UserRole.USER;

    @Column(name = "failed_login_attempts", nullable = false, columnDefinition = "integer default 0")
    private int failedLoginAttempts = 0;

    @Column(name = "locked_until", columnDefinition = "timestamp")
    private LocalDateTime lockedUntil;

    @Column(name = "last_login_at", columnDefinition = "timestamp")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_activity_at", columnDefinition = "timestamp")
    private LocalDateTime lastActivityAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamp")
    private LocalDateTime createdAt;

    public User() {
    }

    public User(String username, String password, String fullName, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(LocalDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static User.UserBuilder builder() {
        return new User.UserBuilder();
    }

    public static class UserBuilder {
        private Long id;
        private String username;
        private String password;
        private String fullName;
        private String phoneNumber;
        private UserRole role = UserRole.USER;
        private int failedLoginAttempts = 0;
        private LocalDateTime lockedUntil;
        private LocalDateTime lastLoginAt;
        private LocalDateTime lastActivityAt;
        private LocalDateTime createdAt;

        UserBuilder() {
        }

        public User.UserBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        public User.UserBuilder username(final String username) {
            this.username = username;
            return this;
        }

        public User.UserBuilder password(final String password) {
            this.password = password;
            return this;
        }

        public User.UserBuilder fullName(final String fullName) {
            this.fullName = fullName;
            return this;
        }

        public User.UserBuilder phoneNumber(final String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public User.UserBuilder role(final UserRole role) {
            this.role = role;
            return this;
        }

        public User.UserBuilder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public User.UserBuilder failedLoginAttempts(final int failedLoginAttempts) {
            this.failedLoginAttempts = failedLoginAttempts;
            return this;
        }

        public User.UserBuilder lockedUntil(final LocalDateTime lockedUntil) {
            this.lockedUntil = lockedUntil;
            return this;
        }

        public User.UserBuilder lastLoginAt(final LocalDateTime lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
            return this;
        }

        public User.UserBuilder lastActivityAt(final LocalDateTime lastActivityAt) {
            this.lastActivityAt = lastActivityAt;
            return this;
        }

        public User build() {
            User user = new User(this.id, this.username, this.password, this.fullName, this.phoneNumber, this.createdAt);
            user.setRole(this.role);
            user.setFailedLoginAttempts(this.failedLoginAttempts);
            user.setLockedUntil(this.lockedUntil);
            user.setLastLoginAt(this.lastLoginAt);
            user.setLastActivityAt(this.lastActivityAt);
            return user;
        }

        public String toString() {
            return "User.UserBuilder(id=" + this.id + ", username=" + this.username + ", password=" + this.password + ", fullName=" + this.fullName + ", phoneNumber=" + this.phoneNumber + ", createdAt=" + this.createdAt + ")";
        }
    }

    public User(Long id, String username, String password, String fullName, String phoneNumber, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
    }

    @PrePersist
    @PreUpdate
    void applyDefaults() {
        if (role == null) {
            role = UserRole.USER;
        }
        if (failedLoginAttempts < 0) {
            failedLoginAttempts = 0;
        }
    }
}
