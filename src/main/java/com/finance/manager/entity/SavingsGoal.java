package com.finance.manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a savings goal set by a user, tracking progress via
 * net income since the goal's start date.
 */
@Entity
@Table(name = "savings_goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsGoal {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Human-readable name for the goal (e.g., "Emergency Fund"). */
    @Column(nullable = false)
    private String goalName;

    /** The monetary amount the user aims to save. */
    @Column(nullable = false)
    private BigDecimal targetAmount;

    /** The date by which the user intends to reach the target amount. */
    @Column(nullable = false)
    private LocalDate targetDate;

    /** Date from which net income is accumulated towards this goal. */
    @Column(nullable = false)
    private LocalDate startDate;

    /** The user who owns this goal. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
