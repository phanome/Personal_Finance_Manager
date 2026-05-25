package com.finance.manager.controller;

import com.finance.manager.dto.response.MonthlyReportResponse;
import com.finance.manager.dto.response.YearlyReportResponse;
import com.finance.manager.entity.User;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock private ReportService reportService;
    @InjectMocks private ReportController reportController;

    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).username("test@test.com").password("pass").fullName("Test").build();
        userDetails = new CustomUserDetails(user);
    }

    @Test
    @DisplayName("GET /api/reports/monthly/2024/1 → 200 OK")
    void getMonthlyReport() {
        MonthlyReportResponse report = new MonthlyReportResponse(
                1, 2024,
                Map.of("Salary", new BigDecimal("5000.00")),
                Map.of("Food", new BigDecimal("400.00")),
                new BigDecimal("4600.00")
        );

        when(reportService.getMonthlyReport(eq(2024), eq(1), any())).thenReturn(report);

        ResponseEntity<MonthlyReportResponse> response =
                reportController.getMonthlyReport(2024, 1, userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMonth()).isEqualTo(1);
        assertThat(response.getBody().getNetSavings()).isEqualByComparingTo("4600.00");
    }

    @Test
    @DisplayName("GET /api/reports/yearly/2024 → 200 OK")
    void getYearlyReport() {
        YearlyReportResponse report = new YearlyReportResponse(
                2024,
                Map.of("Salary", new BigDecimal("60000.00")),
                Map.of("Food", new BigDecimal("4800.00")),
                new BigDecimal("55200.00")
        );

        when(reportService.getYearlyReport(eq(2024), any())).thenReturn(report);

        ResponseEntity<YearlyReportResponse> response =
                reportController.getYearlyReport(2024, userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getYear()).isEqualTo(2024);
        assertThat(response.getBody().getNetSavings()).isEqualByComparingTo("55200.00");
    }
}
