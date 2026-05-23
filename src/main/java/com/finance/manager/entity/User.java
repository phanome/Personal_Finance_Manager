package com.finance.manager.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a registered user of the Personal Finance Manager system.
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user's login email (unique). */
    @Column(unique = true, nullable = false)
    private String username;

    /** BCrypt-encoded password. */
    @Column(nullable = false)
    private String password;

    /** User's full name. */
    @Column(nullable = false)
    private String fullName;

    /** User's contact phone number. */
    @Column(nullable = false)
    private String phoneNumber;
}
