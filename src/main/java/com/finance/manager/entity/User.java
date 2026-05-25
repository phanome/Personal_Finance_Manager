package com.finance.manager.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    @Column(unique = true, nullable = false)
    private String username;

   
    @Column(nullable = false)
    private String password;

   
    @Column(nullable = false)
    private String fullName;

   
    @Column(nullable = false)
    private String phoneNumber;
}
