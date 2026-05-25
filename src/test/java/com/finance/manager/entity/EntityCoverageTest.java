package com.finance.manager.entity;

import com.finance.manager.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


class EntityCoverageTest {

    

    @Test
    @DisplayName("User: builder, getters, setters, equals, hashCode, toString")
    void userEntity() {
        User user = User.builder()
                .id(1L)
                .username("test@example.com")
                .password("pass")
                .fullName("Test User")
                .phoneNumber("+1234567890")
                .build();

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("pass");
        assertThat(user.getFullName()).isEqualTo("Test User");
        assertThat(user.getPhoneNumber()).isEqualTo("+1234567890");

        user.setId(2L);
        user.setUsername("other@example.com");
        user.setPassword("newPass");
        user.setFullName("Other");
        user.setPhoneNumber("+0987654321");

        assertThat(user.getId()).isEqualTo(2L);

       
        User user2 = User.builder().id(2L).username("other@example.com").password("newPass")
                .fullName("Other").phoneNumber("+0987654321").build();
        assertThat(user).isEqualTo(user2);
        assertThat(user.hashCode()).isEqualTo(user2.hashCode());

       
        assertThat(user.toString()).contains("User");

       
        User empty = new User();
        assertThat(empty.getId()).isNull();
    }

    
    @Test
    @DisplayName("Category: builder, getters, setters, equals, hashCode, toString")
    void categoryEntity() {
        User user = User.builder().id(1L).build();

        Category cat = Category.builder()
                .id(1L)
                .name("Salary")
                .type(TransactionType.INCOME)
                .custom(false)
                .user(user)
                .build();

        assertThat(cat.getId()).isEqualTo(1L);
        assertThat(cat.getName()).isEqualTo("Salary");
        assertThat(cat.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(cat.isCustom()).isFalse();
        assertThat(cat.getUser()).isSameAs(user);

        cat.setId(2L);
        cat.setName("Food");
        cat.setType(TransactionType.EXPENSE);
        cat.setCustom(true);
        cat.setUser(null);

        assertThat(cat.getName()).isEqualTo("Food");
        assertThat(cat.isCustom()).isTrue();

        Category cat2 = Category.builder().id(2L).name("Food")
                .type(TransactionType.EXPENSE).custom(true).build();
        assertThat(cat).isEqualTo(cat2);
        assertThat(cat.hashCode()).isEqualTo(cat2.hashCode());
        assertThat(cat.toString()).contains("Category");

        Category empty = new Category();
        assertThat(empty.getId()).isNull();
    }

   

    @Test
    @DisplayName("Transaction: builder, getters, setters, equals, hashCode, toString")
    void transactionEntity() {
        User user = User.builder().id(1L).build();
        Category cat = Category.builder().id(1L).name("Salary").type(TransactionType.INCOME).build();

        Transaction txn = Transaction.builder()
                .id(1L)
                .amount(new BigDecimal("5000.00"))
                .description("Jan Salary")
                .date(LocalDate.of(2024, 1, 15))
                .category(cat)
                .user(user)
                .deleted(false)
                .build();

        assertThat(txn.getId()).isEqualTo(1L);
        assertThat(txn.getAmount()).isEqualByComparingTo("5000.00");
        assertThat(txn.getDescription()).isEqualTo("Jan Salary");
        assertThat(txn.getDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(txn.getCategory()).isSameAs(cat);
        assertThat(txn.getUser()).isSameAs(user);
        assertThat(txn.isDeleted()).isFalse();

        txn.setId(2L);
        txn.setAmount(new BigDecimal("6000.00"));
        txn.setDescription("Updated");
        txn.setDate(LocalDate.of(2024, 2, 1));
        txn.setCategory(null);
        txn.setUser(null);
        txn.setDeleted(true);

        assertThat(txn.isDeleted()).isTrue();

        Transaction txn2 = Transaction.builder().id(2L).amount(new BigDecimal("6000.00"))
                .description("Updated").date(LocalDate.of(2024, 2, 1))
                .deleted(true).build();
        assertThat(txn).isEqualTo(txn2);
        assertThat(txn.hashCode()).isEqualTo(txn2.hashCode());
        assertThat(txn.toString()).contains("Transaction");

        Transaction empty = new Transaction();
        assertThat(empty.getId()).isNull();
    }

    

    @Test
    @DisplayName("SavingsGoal: builder, getters, setters, equals, hashCode, toString")
    void savingsGoalEntity() {
        User user = User.builder().id(1L).build();

        SavingsGoal goal = SavingsGoal.builder()
                .id(1L)
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("5000.00"))
                .targetDate(LocalDate.of(2026, 1, 1))
                .startDate(LocalDate.of(2025, 1, 1))
                .user(user)
                .build();

        assertThat(goal.getId()).isEqualTo(1L);
        assertThat(goal.getGoalName()).isEqualTo("Emergency Fund");
        assertThat(goal.getTargetAmount()).isEqualByComparingTo("5000.00");
        assertThat(goal.getTargetDate()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(goal.getStartDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(goal.getUser()).isSameAs(user);

        goal.setId(2L);
        goal.setGoalName("New Car");
        goal.setTargetAmount(new BigDecimal("20000.00"));
        goal.setTargetDate(LocalDate.of(2027, 6, 1));
        goal.setStartDate(LocalDate.of(2025, 6, 1));
        goal.setUser(null);

        assertThat(goal.getGoalName()).isEqualTo("New Car");

        SavingsGoal goal2 = SavingsGoal.builder().id(2L).goalName("New Car")
                .targetAmount(new BigDecimal("20000.00"))
                .targetDate(LocalDate.of(2027, 6, 1))
                .startDate(LocalDate.of(2025, 6, 1)).build();
        assertThat(goal).isEqualTo(goal2);
        assertThat(goal.hashCode()).isEqualTo(goal2.hashCode());
        assertThat(goal.toString()).contains("SavingsGoal");

        SavingsGoal empty = new SavingsGoal();
        assertThat(empty.getId()).isNull();
    }
}
