package com.finance.manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a single financial transaction (income or expense) for a user.
 */
@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Positive monetary value of the transaction. */
    @Column(nullable = false)
    private BigDecimal amount;

    /** Date on which the transaction occurred; cannot be a future date. */
    @Column(nullable = false)
    private LocalDate date;

    /** Category classifying this transaction. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /** Optional free-text description of the transaction. */
    private String description;

    /** The user who owns this transaction. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Soft-delete flag. When {@code true} the transaction is logically deleted
     * and excluded from reports and savings goal calculations.
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;
}
