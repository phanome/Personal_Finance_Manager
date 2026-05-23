package com.finance.manager.controller;

import com.finance.manager.dto.request.GoalRequest;
import com.finance.manager.dto.request.UpdateGoalRequest;
import com.finance.manager.dto.response.GoalListResponse;
import com.finance.manager.dto.response.GoalResponse;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.entity.User;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for savings goal management.
 * All endpoints require an authenticated session.
 */
@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    /**
     * Creates a new savings goal.
     *
     * @param request     goal creation payload
     * @param userDetails the authenticated user principal
     * @return 201 Created with the new goal and initial progress
     */
    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @Valid @RequestBody GoalRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        GoalResponse response = goalService.createGoal(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Returns all savings goals for the authenticated user.
     *
     * @param userDetails the authenticated user principal
     * @return 200 OK with goal list and live progress metrics
     */
    @GetMapping
    public ResponseEntity<GoalListResponse> getAllGoals(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        return ResponseEntity.ok(goalService.getAllGoals(user));
    }

    /**
     * Retrieves a single savings goal by id.
     *
     * @param id          the goal id
     * @param userDetails the authenticated user principal
     * @return 200 OK with goal and live progress, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoal(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        return ResponseEntity.ok(goalService.getGoal(id, user));
    }

    /**
     * Updates a savings goal's target amount and/or target date.
     *
     * @param id          the goal id
     * @param request     fields to update
     * @param userDetails the authenticated user principal
     * @return 200 OK with the updated goal and recalculated progress
     */
    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGoalRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        GoalResponse response = goalService.updateGoal(id, request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a savings goal.
     *
     * @param id          the goal id
     * @param userDetails the authenticated user principal
     * @return 200 OK with confirmation message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteGoal(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        MessageResponse response = goalService.deleteGoal(id, user);
        return ResponseEntity.ok(response);
    }
}
