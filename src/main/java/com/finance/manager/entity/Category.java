package com.finance.manager.entity;

import com.finance.manager.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a financial category (e.g., Salary, Food, Rent).
 * Default categories are shared across all users; custom categories belong to a single user.
 */
@Entity
@Table(name = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Category display name. */
    @Column(nullable = false)
    private String name;

    /** Whether this category represents income or an expense. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    /**
     * {@code true} when this is a user-defined category,
     * {@code false} for system-wide default categories.
     */
    @Column(nullable = false)
    private boolean custom;

    /**
     * The owning user for custom categories; {@code null} for default categories.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
