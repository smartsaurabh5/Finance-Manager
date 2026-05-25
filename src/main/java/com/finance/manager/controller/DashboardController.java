package com.finance.manager.controller;

import com.finance.manager.dto.DashboardSummaryResponse;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final ReportService reportService;

    public DashboardController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(reportService.getDashboardSummary(userDetails.getUser()));
    }
}
