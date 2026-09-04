package com.finance.manager.dto;

import com.finance.manager.dto.request.*;
import com.finance.manager.dto.response.*;
import com.finance.manager.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises Lombok-generated methods (getters, setters, equals, hashCode, toString)
 * on all DTOs to achieve coverage of generated code.
 */
class DtoCoverageTest {

    // =======================================================================
    // Request DTOs
    // =======================================================================

    @Test
    @DisplayName("LoginRequest: getters, setters, equals, hashCode, toString")
    void loginRequest() {
        LoginRequest a = new LoginRequest();
        a.setUsername("user@test.com");
        a.setPassword("pass");

        assertThat(a.getUsername()).isEqualTo("user@test.com");
        assertThat(a.getPassword()).isEqualTo("pass");

        LoginRequest b = new LoginRequest();
        b.setUsername("user@test.com");
        b.setPassword("pass");

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a.toString()).contains("LoginRequest");
    }

    @Test
    @DisplayName("RegisterRequest: getters, setters, equals, hashCode, toString")
    void registerRequest() {
        RegisterRequest r = new RegisterRequest();
        r.setUsername("user@test.com");
        r.setPassword("pass123");
        r.setFullName("Test User");
        r.setPhoneNumber("+1234567890");

        assertThat(r.getUsername()).isEqualTo("user@test.com");
        assertThat(r.getPassword()).isEqualTo("pass123");
        assertThat(r.getFullName()).isEqualTo("Test User");
        assertThat(r.getPhoneNumber()).isEqualTo("+1234567890");
        assertThat(r.toString()).contains("RegisterRequest");

        RegisterRequest r2 = new RegisterRequest();
        r2.setUsername("user@test.com");
        r2.setPassword("pass123");
        r2.setFullName("Test User");
        r2.setPhoneNumber("+1234567890");
        assertThat(r).isEqualTo(r2);
        assertThat(r.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    @DisplayName("CategoryRequest: getters, setters, equals, hashCode, toString")
    void categoryRequest() {
        CategoryRequest c = new CategoryRequest();
        c.setName("Food");
        c.setType(TransactionType.EXPENSE);

        assertThat(c.getName()).isEqualTo("Food");
        assertThat(c.getType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(c.toString()).contains("CategoryRequest");

        CategoryRequest c2 = new CategoryRequest();
        c2.setName("Food");
        c2.setType(TransactionType.EXPENSE);
        assertThat(c).isEqualTo(c2);
        assertThat(c.hashCode()).isEqualTo(c2.hashCode());
    }

    @Test
    @DisplayName("TransactionRequest: getters, setters, equals, hashCode, toString")
    void transactionRequest() {
        TransactionRequest t = new TransactionRequest();
        t.setAmount(new BigDecimal("100.00"));
        t.setDate(LocalDate.of(2024, 1, 1));
        t.setCategory("Salary");
        t.setDescription("pay");

        assertThat(t.getAmount()).isEqualByComparingTo("100.00");
        assertThat(t.getDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(t.getCategory()).isEqualTo("Salary");
        assertThat(t.getDescription()).isEqualTo("pay");
        assertThat(t.toString()).contains("TransactionRequest");

        TransactionRequest t2 = new TransactionRequest();
        t2.setAmount(new BigDecimal("100.00"));
        t2.setDate(LocalDate.of(2024, 1, 1));
        t2.setCategory("Salary");
        t2.setDescription("pay");
        assertThat(t).isEqualTo(t2);
        assertThat(t.hashCode()).isEqualTo(t2.hashCode());
    }

    @Test
    @DisplayName("UpdateTransactionRequest: getters, setters, equals, hashCode, toString")
    void updateTransactionRequest() {
        UpdateTransactionRequest u = new UpdateTransactionRequest();
        u.setAmount(new BigDecimal("200.00"));
        u.setCategory("Food");
        u.setDescription("lunch");

        assertThat(u.getAmount()).isEqualByComparingTo("200.00");
        assertThat(u.getCategory()).isEqualTo("Food");
        assertThat(u.getDescription()).isEqualTo("lunch");
        assertThat(u.toString()).contains("UpdateTransactionRequest");

        UpdateTransactionRequest u2 = new UpdateTransactionRequest();
        u2.setAmount(new BigDecimal("200.00"));
        u2.setCategory("Food");
        u2.setDescription("lunch");
        assertThat(u).isEqualTo(u2);
        assertThat(u.hashCode()).isEqualTo(u2.hashCode());
    }

    @Test
    @DisplayName("GoalRequest: getters, setters, equals, hashCode, toString")
    void goalRequest() {
        GoalRequest g = new GoalRequest();
        g.setGoalName("Emergency");
        g.setTargetAmount(new BigDecimal("5000.00"));
        g.setTargetDate(LocalDate.of(2026, 1, 1));
        g.setStartDate(LocalDate.of(2025, 1, 1));

        assertThat(g.getGoalName()).isEqualTo("Emergency");
        assertThat(g.getTargetAmount()).isEqualByComparingTo("5000.00");
        assertThat(g.getTargetDate()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(g.getStartDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(g.toString()).contains("GoalRequest");

        GoalRequest g2 = new GoalRequest();
        g2.setGoalName("Emergency");
        g2.setTargetAmount(new BigDecimal("5000.00"));
        g2.setTargetDate(LocalDate.of(2026, 1, 1));
        g2.setStartDate(LocalDate.of(2025, 1, 1));
        assertThat(g).isEqualTo(g2);
        assertThat(g.hashCode()).isEqualTo(g2.hashCode());
    }

    @Test
    @DisplayName("UpdateGoalRequest: getters, setters, equals, hashCode, toString")
    void updateGoalRequest() {
        UpdateGoalRequest u = new UpdateGoalRequest();
        u.setTargetAmount(new BigDecimal("6000.00"));
        u.setTargetDate(LocalDate.of(2027, 1, 1));

        assertThat(u.getTargetAmount()).isEqualByComparingTo("6000.00");
        assertThat(u.getTargetDate()).isEqualTo(LocalDate.of(2027, 1, 1));
        assertThat(u.toString()).contains("UpdateGoalRequest");

        UpdateGoalRequest u2 = new UpdateGoalRequest();
        u2.setTargetAmount(new BigDecimal("6000.00"));
        u2.setTargetDate(LocalDate.of(2027, 1, 1));
        assertThat(u).isEqualTo(u2);
        assertThat(u.hashCode()).isEqualTo(u2.hashCode());
    }

    // =======================================================================
    // Response DTOs
    // =======================================================================

    @Test
    @DisplayName("MessageResponse: getter, setter, equals, hashCode, toString")
    void messageResponse() {
        MessageResponse m = new MessageResponse("ok");
        assertThat(m.getMessage()).isEqualTo("ok");
        m.setMessage("updated");
        assertThat(m.getMessage()).isEqualTo("updated");
        assertThat(m.toString()).contains("MessageResponse");

        MessageResponse m2 = new MessageResponse("updated");
        assertThat(m).isEqualTo(m2);
        assertThat(m.hashCode()).isEqualTo(m2.hashCode());

        // no-args
        MessageResponse m3 = new MessageResponse();
        m3.setMessage("test");
        assertThat(m3.getMessage()).isEqualTo("test");
    }

    @Test
    @DisplayName("RegisterResponse: getter, setter, equals, hashCode, toString")
    void registerResponse() {
        RegisterResponse r = new RegisterResponse("created", 1L);
        assertThat(r.getMessage()).isEqualTo("created");
        assertThat(r.getUserId()).isEqualTo(1L);
        r.setMessage("updated");
        r.setUserId(2L);
        assertThat(r.toString()).contains("RegisterResponse");

        RegisterResponse r2 = new RegisterResponse("updated", 2L);
        assertThat(r).isEqualTo(r2);
        assertThat(r.hashCode()).isEqualTo(r2.hashCode());

        // no-args
        RegisterResponse r3 = new RegisterResponse();
        r3.setMessage("test");
        r3.setUserId(3L);
        assertThat(r3.getUserId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("CategoryResponse: all-args (3 fields), no-args, getters, setters")
    void categoryResponse() {
        // CategoryResponse has 3 fields: name, type, custom (no id)
        CategoryResponse c = new CategoryResponse("Salary", "INCOME", false);
        assertThat(c.getName()).isEqualTo("Salary");
        assertThat(c.getType()).isEqualTo("INCOME");
        assertThat(c.isCustom()).isFalse();

        CategoryResponse c2 = new CategoryResponse();
        c2.setName("Salary");
        c2.setType("INCOME");
        c2.setCustom(false);

        assertThat(c).isEqualTo(c2);
        assertThat(c.hashCode()).isEqualTo(c2.hashCode());
        assertThat(c.toString()).contains("CategoryResponse");
    }

    @Test
    @DisplayName("CategoryListResponse: all-args, no-args, getters, setters")
    void categoryListResponse() {
        CategoryResponse cat = new CategoryResponse("Salary", "INCOME", false);
        CategoryListResponse cl = new CategoryListResponse(List.of(cat));
        assertThat(cl.getCategories()).hasSize(1);

        CategoryListResponse cl2 = new CategoryListResponse();
        cl2.setCategories(List.of(cat));

        assertThat(cl).isEqualTo(cl2);
        assertThat(cl.hashCode()).isEqualTo(cl2.hashCode());
        assertThat(cl.toString()).contains("CategoryListResponse");
    }

    @Test
    @DisplayName("TransactionResponse: all-args, no-args, getters, setters")
    void transactionResponse() {
        TransactionResponse t = new TransactionResponse(1L, new BigDecimal("100"), "desc",
                LocalDate.of(2024, 1, 1), "INCOME", "Salary");
        assertThat(t.getId()).isEqualTo(1L);
        assertThat(t.getAmount()).isEqualByComparingTo("100");
        assertThat(t.getDescription()).isEqualTo("desc");

        TransactionResponse t2 = new TransactionResponse();
        t2.setId(1L);
        t2.setAmount(new BigDecimal("100"));
        t2.setDescription("desc");
        t2.setDate(LocalDate.of(2024, 1, 1));
        t2.setType("INCOME");
        t2.setCategory("Salary");

        assertThat(t).isEqualTo(t2);
        assertThat(t.hashCode()).isEqualTo(t2.hashCode());
        assertThat(t.toString()).contains("TransactionResponse");
    }

    @Test
    @DisplayName("TransactionListResponse: all-args, no-args, getters, setters")
    void transactionListResponse() {
        TransactionListResponse tl = new TransactionListResponse(List.of());
        assertThat(tl.getTransactions()).isEmpty();

        TransactionListResponse tl2 = new TransactionListResponse();
        tl2.setTransactions(List.of());

        assertThat(tl).isEqualTo(tl2);
        assertThat(tl.hashCode()).isEqualTo(tl2.hashCode());
        assertThat(tl.toString()).contains("TransactionListResponse");
    }

    @Test
    @DisplayName("GoalResponse: all-args, no-args, getters, setters")
    void goalResponse() {
        GoalResponse g = new GoalResponse(1L, "Fund", new BigDecimal("5000"),
                LocalDate.of(2026, 1, 1), LocalDate.of(2025, 1, 1),
                new BigDecimal("800"), 16.0, new BigDecimal("4200"));
        assertThat(g.getId()).isEqualTo(1L);
        assertThat(g.getGoalName()).isEqualTo("Fund");
        assertThat(g.getProgressPercentage()).isEqualTo(16.0);
        assertThat(g.getRemainingAmount()).isEqualByComparingTo("4200");

        GoalResponse g2 = new GoalResponse();
        g2.setId(1L);
        g2.setGoalName("Fund");
        g2.setTargetAmount(new BigDecimal("5000"));
        g2.setTargetDate(LocalDate.of(2026, 1, 1));
        g2.setStartDate(LocalDate.of(2025, 1, 1));
        g2.setCurrentProgress(new BigDecimal("800"));
        g2.setProgressPercentage(16.0);
        g2.setRemainingAmount(new BigDecimal("4200"));

        assertThat(g).isEqualTo(g2);
        assertThat(g.hashCode()).isEqualTo(g2.hashCode());
        assertThat(g.toString()).contains("GoalResponse");
    }

    @Test
    @DisplayName("GoalListResponse: all-args, no-args, getters, setters")
    void goalListResponse() {
        GoalListResponse gl = new GoalListResponse(List.of());
        assertThat(gl.getGoals()).isEmpty();

        GoalListResponse gl2 = new GoalListResponse();
        gl2.setGoals(List.of());

        assertThat(gl).isEqualTo(gl2);
        assertThat(gl.hashCode()).isEqualTo(gl2.hashCode());
        assertThat(gl.toString()).contains("GoalListResponse");
    }

    @Test
    @DisplayName("MonthlyReportResponse: all-args, no-args, getters, setters")
    void monthlyReportResponse() {
        MonthlyReportResponse mr = new MonthlyReportResponse(1, 2024,
                Map.of("Salary", new BigDecimal("5000")),
                Map.of("Food", new BigDecimal("400")),
                new BigDecimal("4600"));
        assertThat(mr.getMonth()).isEqualTo(1);
        assertThat(mr.getYear()).isEqualTo(2024);
        assertThat(mr.getNetSavings()).isEqualByComparingTo("4600");

        MonthlyReportResponse mr2 = new MonthlyReportResponse();
        mr2.setMonth(1);
        mr2.setYear(2024);
        mr2.setTotalIncome(Map.of("Salary", new BigDecimal("5000")));
        mr2.setTotalExpenses(Map.of("Food", new BigDecimal("400")));
        mr2.setNetSavings(new BigDecimal("4600"));

        assertThat(mr).isEqualTo(mr2);
        assertThat(mr.hashCode()).isEqualTo(mr2.hashCode());
        assertThat(mr.toString()).contains("MonthlyReportResponse");
    }

    @Test
    @DisplayName("YearlyReportResponse: all-args, no-args, getters, setters")
    void yearlyReportResponse() {
        YearlyReportResponse yr = new YearlyReportResponse(2024,
                Map.of("Salary", new BigDecimal("60000")),
                Map.of("Food", new BigDecimal("4800")),
                new BigDecimal("55200"));
        assertThat(yr.getYear()).isEqualTo(2024);
        assertThat(yr.getNetSavings()).isEqualByComparingTo("55200");

        YearlyReportResponse yr2 = new YearlyReportResponse();
        yr2.setYear(2024);
        yr2.setTotalIncome(Map.of("Salary", new BigDecimal("60000")));
        yr2.setTotalExpenses(Map.of("Food", new BigDecimal("4800")));
        yr2.setNetSavings(new BigDecimal("55200"));

        assertThat(yr).isEqualTo(yr2);
        assertThat(yr.hashCode()).isEqualTo(yr2.hashCode());
        assertThat(yr.toString()).contains("YearlyReportResponse");
    }
}
