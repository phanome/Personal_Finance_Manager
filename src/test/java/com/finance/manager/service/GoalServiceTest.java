package com.finance.manager.service;

import com.finance.manager.dto.request.GoalRequest;
import com.finance.manager.dto.request.UpdateGoalRequest;
import com.finance.manager.dto.response.GoalListResponse;
import com.finance.manager.dto.response.GoalResponse;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.entity.SavingsGoal;
import com.finance.manager.entity.User;
import com.finance.manager.enums.TransactionType;
import com.finance.manager.exception.ResourceNotFoundException;
import com.finance.manager.repository.SavingsGoalRepository;
import com.finance.manager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GoalService}.
 */
@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock private SavingsGoalRepository goalRepository;
    @Mock private TransactionRepository transactionRepository;

    @InjectMocks
    private GoalService goalService;

    private User user;
    private SavingsGoal sampleGoal;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("test@example.com").build();

        sampleGoal = SavingsGoal.builder()
                .id(1L)
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("5000.00"))
                .targetDate(LocalDate.now().plusYears(1))
                .startDate(LocalDate.of(2025, 1, 1))
                .user(user)
                .build();
    }

    private void mockProgressQueries(BigDecimal income, BigDecimal expense) {
        when(transactionRepository.sumAmountByUserAndTypeFromDate(eq(user), eq(TransactionType.INCOME), any()))
                .thenReturn(income);
        when(transactionRepository.sumAmountByUserAndTypeFromDate(eq(user), eq(TransactionType.EXPENSE), any()))
                .thenReturn(expense);
    }

    // -----------------------------------------------------------------------
    // createGoal
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createGoal - success with explicit startDate")
    void createGoal_success_withStartDate() {
        GoalRequest request = new GoalRequest();
        request.setGoalName("Emergency Fund");
        request.setTargetAmount(new BigDecimal("5000.00"));
        request.setTargetDate(LocalDate.now().plusYears(1));
        request.setStartDate(LocalDate.of(2025, 1, 1));

        when(goalRepository.save(any(SavingsGoal.class))).thenReturn(sampleGoal);
        mockProgressQueries(new BigDecimal("1000.00"), new BigDecimal("200.00"));

        GoalResponse response = goalService.createGoal(request, user);

        assertThat(response.getGoalName()).isEqualTo("Emergency Fund");
        assertThat(response.getCurrentProgress()).isEqualByComparingTo("800.00");
        assertThat(response.getProgressPercentage()).isEqualTo(16.0);
    }

    @Test
    @DisplayName("createGoal - defaults startDate to today when not provided")
    void createGoal_defaultStartDate() {
        GoalRequest request = new GoalRequest();
        request.setGoalName("New Car");
        request.setTargetAmount(new BigDecimal("10000.00"));
        request.setTargetDate(LocalDate.now().plusYears(2));
        // startDate not set

        SavingsGoal savedGoal = SavingsGoal.builder()
                .id(2L).goalName("New Car")
                .targetAmount(new BigDecimal("10000.00"))
                .targetDate(LocalDate.now().plusYears(2))
                .startDate(LocalDate.now())
                .user(user).build();

        when(goalRepository.save(any(SavingsGoal.class))).thenReturn(savedGoal);
        mockProgressQueries(BigDecimal.ZERO, BigDecimal.ZERO);

        GoalResponse response = goalService.createGoal(request, user);

        assertThat(response.getGoalName()).isEqualTo("New Car");
        assertThat(response.getCurrentProgress()).isEqualByComparingTo("0.00");
    }

    // -----------------------------------------------------------------------
    // getAllGoals
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getAllGoals - returns all goals with progress")
    void getAllGoals_returnsGoals() {
        when(goalRepository.findByUser(user)).thenReturn(List.of(sampleGoal));
        mockProgressQueries(new BigDecimal("3000.00"), new BigDecimal("1000.00"));

        GoalListResponse response = goalService.getAllGoals(user);

        assertThat(response.getGoals()).hasSize(1);
        assertThat(response.getGoals().get(0).getCurrentProgress()).isEqualByComparingTo("2000.00");
    }

    // -----------------------------------------------------------------------
    // getGoal
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getGoal - success: returns goal with progress")
    void getGoal_success() {
        when(goalRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(sampleGoal));
        mockProgressQueries(new BigDecimal("1000.00"), BigDecimal.ZERO);

        GoalResponse response = goalService.getGoal(1L, user);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCurrentProgress()).isEqualByComparingTo("1000.00");
    }

    @Test
    @DisplayName("getGoal - not found: throws ResourceNotFoundException")
    void getGoal_notFound() {
        when(goalRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.getGoal(99L, user))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // -----------------------------------------------------------------------
    // updateGoal
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("updateGoal - success: target amount and date updated")
    void updateGoal_success() {
        UpdateGoalRequest request = new UpdateGoalRequest();
        request.setTargetAmount(new BigDecimal("6000.00"));
        request.setTargetDate(LocalDate.now().plusYears(2));

        SavingsGoal updatedGoal = SavingsGoal.builder()
                .id(1L).goalName("Emergency Fund")
                .targetAmount(new BigDecimal("6000.00"))
                .targetDate(LocalDate.now().plusYears(2))
                .startDate(LocalDate.of(2025, 1, 1))
                .user(user).build();

        when(goalRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(sampleGoal));
        when(goalRepository.save(any(SavingsGoal.class))).thenReturn(updatedGoal);
        mockProgressQueries(new BigDecimal("1000.00"), BigDecimal.ZERO);

        GoalResponse response = goalService.updateGoal(1L, request, user);

        assertThat(response.getTargetAmount()).isEqualByComparingTo("6000.00");
    }

    // -----------------------------------------------------------------------
    // deleteGoal
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteGoal - success: goal removed")
    void deleteGoal_success() {
        when(goalRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(sampleGoal));

        MessageResponse response = goalService.deleteGoal(1L, user);

        assertThat(response.getMessage()).isEqualTo("Goal deleted successfully");
        verify(goalRepository).delete(sampleGoal);
    }

    @Test
    @DisplayName("deleteGoal - not found: throws ResourceNotFoundException")
    void deleteGoal_notFound() {
        when(goalRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.deleteGoal(99L, user))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // -----------------------------------------------------------------------
    // Progress edge cases
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("progress clamped to 0 when expenses exceed income")
    void progress_clampedToZero_whenNegative() {
        when(goalRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(sampleGoal));
        // Expenses > Income => net negative
        mockProgressQueries(new BigDecimal("100.00"), new BigDecimal("500.00"));

        GoalResponse response = goalService.getGoal(1L, user);

        assertThat(response.getCurrentProgress()).isEqualByComparingTo("0.00");
        assertThat(response.getProgressPercentage()).isEqualTo(0.0);
    }
}
