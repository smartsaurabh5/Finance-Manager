package com.finance.manager.dto;

public class LoginResponse {
    private String message;
    private String tokenType;
    private String accessToken;
    private long expiresAt;

    public LoginResponse(String message, String tokenType, String accessToken, long expiresAt) {
        this.message = message;
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
    }

    public String getMessage() {
        return message;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresAt() {
        return expiresAt;
    }
}
