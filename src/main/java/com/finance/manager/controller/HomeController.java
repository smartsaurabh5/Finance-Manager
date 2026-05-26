package com.finance.manager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {
    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "status", "UP",
                "message", "Finance Manager API is running",
                "swagger", "/swagger-ui/index.html",
                "health", "/health"
        );
    }

    @GetMapping({"/health", "/api/health"})
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "message", "Finance Manager API is running"
        );
    }
}
