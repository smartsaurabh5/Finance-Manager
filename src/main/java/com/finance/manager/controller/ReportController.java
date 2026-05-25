package com.finance.manager.controller;

import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<Map<String, Object>> getMonthlyReport(@PathVariable int year,
                                                                @PathVariable int month,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(reportService.getMonthlyReport(userDetails.getUser(), year, month));
    }

    @GetMapping("/yearly/{year}")
    public ResponseEntity<Map<String, Object>> getYearlyReport(@PathVariable int year,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(reportService.getYearlyReport(userDetails.getUser(), year));
    }

    @GetMapping(value = "/monthly/{year}/{month}/export.csv", produces = "text/csv")
    public ResponseEntity<String> exportMonthlyCsv(@PathVariable int year,
                                                   @PathVariable int month,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=finance-report-" + year + "-" + month + ".csv")
                .body(reportService.exportMonthlyCsv(userDetails.getUser(), year, month));
    }
}
