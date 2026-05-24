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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for savings goal management and progress tracking.
 */
@Service
@Transactional
public class GoalService {

    private final SavingsGoalRepository goalRepository;
    private final TransactionRepository transactionRepository;

    public GoalService(SavingsGoalRepository goalRepository,
                       TransactionRepository transactionRepository) {
        this.goalRepository = goalRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Creates a new savings goal for the authenticated user.
     *
     * @param request goal creation payload
     * @param user    the owning user
     * @return the created goal with current progress
     */
    public GoalResponse createGoal(GoalRequest request, User user) {
        LocalDate startDate = (request.getStartDate() != null)
                ? request.getStartDate()
                : LocalDate.now();

        SavingsGoal goal = SavingsGoal.builder()
                .goalName(request.getGoalName())
                .targetAmount(request.getTargetAmount())
                .targetDate(request.getTargetDate())
                .startDate(startDate)
                .user(user)
                .build();

        SavingsGoal saved = goalRepository.save(goal);
        return buildGoalResponse(saved, user);
    }

    /**
     * Returns all savings goals for the authenticated user with live progress metrics.
     *
     * @param user the authenticated user
     */
    @Transactional(readOnly = true)
    public GoalListResponse getAllGoals(User user) {
        List<GoalResponse> responses = goalRepository.findByUser(user)
                .stream()
                .map(goal -> buildGoalResponse(goal, user))
                .collect(Collectors.toList());
        return new GoalListResponse(responses);
    }

    /**
     * Retrieves a single savings goal by id.
     *
     * @param id   the goal id
     * @param user the authenticated user
     * @return the goal with current progress
     * @throws ResourceNotFoundException if the goal doesn't exist or belongs to another user
     */
    @Transactional(readOnly = true)
    public GoalResponse getGoal(Long id, User user) {
        SavingsGoal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));
        return buildGoalResponse(goal, user);
    }

    /**
     * Updates the target amount and/or target date of an existing goal.
     *
     * @param id      the goal id
     * @param request fields to update
     * @param user    the authenticated user
     * @return the updated goal with recalculated progress
     * @throws ResourceNotFoundException if the goal doesn't exist or belongs to another user
     */
    public GoalResponse updateGoal(Long id, UpdateGoalRequest request, User user) {
        SavingsGoal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));

        if (request.getTargetAmount() != null) {
            goal.setTargetAmount(request.getTargetAmount());
        }
        if (request.getTargetDate() != null) {
            goal.setTargetDate(request.getTargetDate());
        }

        SavingsGoal saved = goalRepository.save(goal);
        return buildGoalResponse(saved, user);
    }

    /**
     * Deletes a savings goal.
     *
     * @param id   the goal id
     * @param user the authenticated user
     * @return success message
     * @throws ResourceNotFoundException if the goal doesn't exist or belongs to another user
     */
    public MessageResponse deleteGoal(Long id, User user) {
        SavingsGoal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));
        goalRepository.delete(goal);
        return new MessageResponse("Goal deleted successfully");
    }

    // -----------------------------------------------------------------------
    // Progress calculation
    // -----------------------------------------------------------------------

    /**
     * Builds a {@link GoalResponse} enriched with live progress metrics.
     *
     * <p>Progress = (total INCOME since startDate) − (total EXPENSE since startDate).
     * Progress is clamped to 0 if net is negative, capped at targetAmount for the percentage.</p>
     */
    private GoalResponse buildGoalResponse(SavingsGoal goal, User user) {
        BigDecimal totalIncome = transactionRepository.sumAmountByUserAndTypeFromDate(
                user, TransactionType.INCOME, goal.getStartDate());
        BigDecimal totalExpense = transactionRepository.sumAmountByUserAndTypeFromDate(
                user, TransactionType.EXPENSE, goal.getStartDate());

        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        BigDecimal currentProgress = totalIncome.subtract(totalExpense);
        if (currentProgress.compareTo(BigDecimal.ZERO) < 0) {
            currentProgress = BigDecimal.ZERO;
        }

        BigDecimal targetAmount = goal.getTargetAmount();

        double progressPct;
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            progressPct = 0.0;
        } else {
            progressPct = currentProgress
                    .multiply(BigDecimal.valueOf(100))
                    .divide(targetAmount, 2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        BigDecimal remaining = targetAmount.subtract(currentProgress);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        BigDecimal currentProgressToReturn = currentProgress.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : currentProgress.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();

        BigDecimal remainingToReturn = remaining.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : remaining.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();

        return new GoalResponse(
                goal.getId(),
                goal.getGoalName(),
                targetAmount,
                goal.getTargetDate(),
                goal.getStartDate(),
                currentProgressToReturn,
                progressPct,
                remainingToReturn);
    }
}
