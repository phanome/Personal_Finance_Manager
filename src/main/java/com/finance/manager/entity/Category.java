package com.finance.manager.entity;

import com.finance.manager.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(nullable = false)
    private String name;

    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    
    @Column(nullable = false)
    private boolean custom;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
