package com.finance.manager.controller;

import com.finance.manager.dto.response.MonthlyReportResponse;
import com.finance.manager.dto.response.YearlyReportResponse;
import com.finance.manager.entity.User;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for financial report generation.
 * All endpoints require an authenticated session.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Generates a monthly report showing income, expenses, and net savings.
     *
     * @param year        the 4-digit calendar year (e.g. 2024)
     * @param month       the month number (1–12)
     * @param userDetails the authenticated user principal
     * @return 200 OK with the monthly report
     */
    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            @PathVariable int year,
            @PathVariable int month,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        MonthlyReportResponse response = reportService.getMonthlyReport(year, month, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Generates a yearly report aggregating all months of the given year.
     *
     * @param year        the 4-digit calendar year
     * @param userDetails the authenticated user principal
     * @return 200 OK with the yearly report
     */
    @GetMapping("/yearly/{year}")
    public ResponseEntity<YearlyReportResponse> getYearlyReport(
            @PathVariable int year,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        YearlyReportResponse response = reportService.getYearlyReport(year, user);
        return ResponseEntity.ok(response);
    }
}
