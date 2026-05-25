package com.finance.manager.dto;

import java.time.Instant;
import java.util.Map;

public class ErrorResponse {
    private boolean success;
    private String error;
    private String message;
    private int status;
    private String path;
    private Instant timestamp;
    private Map<String, String> validationErrors;

    public ErrorResponse(String error, String message, int status, String path) {
        this.success = false;
        this.error = error;
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = Instant.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
