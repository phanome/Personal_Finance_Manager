package com.finance.manager.controller;

import com.finance.manager.dto.request.GoalRequest;
import com.finance.manager.dto.request.UpdateGoalRequest;
import com.finance.manager.dto.response.GoalListResponse;
import com.finance.manager.dto.response.GoalResponse;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.entity.User;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.GoalService;
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
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GoalControllerTest {

    @Mock private GoalService goalService;
    @InjectMocks private GoalController goalController;

    private CustomUserDetails userDetails;
    private GoalResponse sampleGoal;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).username("test@test.com").password("pass").fullName("Test").build();
        userDetails = new CustomUserDetails(user);

        sampleGoal = new GoalResponse(1L, "Emergency Fund",
                new BigDecimal("5000.00"), LocalDate.now().plusYears(1),
                LocalDate.of(2025, 1, 1),
                new BigDecimal("800.00"), 16.0,
                new BigDecimal("4200.00"));
    }

    @Test
    @DisplayName("POST /api/goals → 201 Created")
    void createGoal() {
        GoalRequest req = new GoalRequest();
        req.setGoalName("Emergency Fund");
        req.setTargetAmount(new BigDecimal("5000.00"));
        req.setTargetDate(LocalDate.now().plusYears(1));

        when(goalService.createGoal(any(), any())).thenReturn(sampleGoal);

        ResponseEntity<GoalResponse> response = goalController.createGoal(req, userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getGoalName()).isEqualTo("Emergency Fund");
    }

    @Test
    @DisplayName("GET /api/goals → 200 OK")
    void getAllGoals() {
        when(goalService.getAllGoals(any()))
                .thenReturn(new GoalListResponse(List.of(sampleGoal)));

        ResponseEntity<GoalListResponse> response = goalController.getAllGoals(userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getGoals()).hasSize(1);
    }

    @Test
    @DisplayName("GET /api/goals/1 → 200 OK")
    void getGoal() {
        when(goalService.getGoal(eq(1L), any())).thenReturn(sampleGoal);

        ResponseEntity<GoalResponse> response = goalController.getGoal(1L, userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("PUT /api/goals/1 → 200 OK")
    void updateGoal() {
        UpdateGoalRequest req = new UpdateGoalRequest();
        req.setTargetAmount(new BigDecimal("6000.00"));

        when(goalService.updateGoal(eq(1L), any(), any())).thenReturn(sampleGoal);

        ResponseEntity<GoalResponse> response = goalController.updateGoal(1L, req, userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getGoalName()).isEqualTo("Emergency Fund");
    }

    @Test
    @DisplayName("DELETE /api/goals/1 → 200 OK")
    void deleteGoal() {
        when(goalService.deleteGoal(eq(1L), any()))
                .thenReturn(new MessageResponse("Goal deleted successfully"));

        ResponseEntity<MessageResponse> response = goalController.deleteGoal(1L, userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("Goal deleted successfully");
    }
}
