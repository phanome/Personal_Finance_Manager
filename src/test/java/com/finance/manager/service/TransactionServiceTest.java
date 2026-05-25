package com.finance.manager.service;

import com.finance.manager.dto.request.TransactionRequest;
import com.finance.manager.dto.request.UpdateTransactionRequest;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.dto.response.TransactionListResponse;
import com.finance.manager.dto.response.TransactionResponse;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.enums.TransactionType;
import com.finance.manager.exception.ResourceNotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private CategoryService categoryService;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Category salaryCategory;
    private Transaction sampleTransaction;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("test@example.com").build();

        salaryCategory = Category.builder()
                .id(1L).name("Salary").type(TransactionType.INCOME).custom(false).build();

        sampleTransaction = Transaction.builder()
                .id(1L)
                .amount(new BigDecimal("5000.00"))
                .date(LocalDate.of(2024, 1, 15))
                .category(salaryCategory)
                .description("January Salary")
                .user(user)
                .deleted(false)
                .build();
    }

    
    @Test
    @DisplayName("createTransaction - success: transaction saved and returned")
    void createTransaction_success() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("5000.00"));
        request.setDate(LocalDate.of(2024, 1, 15));
        request.setCategory("Salary");
        request.setDescription("January Salary");

        when(categoryService.resolveCategoryForUser("Salary", user)).thenReturn(salaryCategory);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(sampleTransaction);

        TransactionResponse response = transactionService.createTransaction(request, user);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getAmount()).isEqualByComparingTo("5000.00");
        assertThat(response.getCategory()).isEqualTo("Salary");
        assertThat(response.getType()).isEqualTo("INCOME");
    }

    

    @Test
    @DisplayName("getTransactions - returns filtered list wrapped in response")
    void getTransactions_withFilters() {
        when(transactionRepository.findByFilters(user, null, null, null))
                .thenReturn(List.of(sampleTransaction));

        TransactionListResponse response = transactionService.getTransactions(user, null, null, null);

        assertThat(response.getTransactions()).hasSize(1);
        assertThat(response.getTransactions().get(0).getCategory()).isEqualTo("Salary");
    }

    @Test
    @DisplayName("getTransactions - returns transactions filtered by category name")
    void getTransactions_withCategoryNameFilter() {
        when(categoryService.resolveCategoryForUser("Salary", user)).thenReturn(salaryCategory);
        when(transactionRepository.findByUserAndCategory(user, salaryCategory))
                .thenReturn(List.of(sampleTransaction));

        TransactionListResponse response = transactionService.getTransactions(user, null, null, null, "Salary");

        assertThat(response.getTransactions()).hasSize(1);
        assertThat(response.getTransactions().get(0).getCategory()).isEqualTo("Salary");
    }

    @Test
    @DisplayName("getTransactions - empty result when no transactions")
    void getTransactions_empty() {
        when(transactionRepository.findByFilters(user, null, null, null)).thenReturn(List.of());

        TransactionListResponse response = transactionService.getTransactions(user, null, null, null);

        assertThat(response.getTransactions()).isEmpty();
    }

    

    @Test
    @DisplayName("updateTransaction - success: amount updated, date unchanged")
    void updateTransaction_success() {
        UpdateTransactionRequest request = new UpdateTransactionRequest();
        request.setAmount(new BigDecimal("6000.00"));
        request.setDescription("Updated Salary");

        Transaction updated = Transaction.builder()
                .id(1L).amount(new BigDecimal("6000.00"))
                .date(LocalDate.of(2024, 1, 15))
                .category(salaryCategory).description("Updated Salary")
                .user(user).deleted(false).build();

        when(transactionRepository.findByIdAndUserAndDeletedFalse(1L, user))
                .thenReturn(Optional.of(sampleTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(updated);

        TransactionResponse response = transactionService.updateTransaction(1L, request, user);

        assertThat(response.getAmount()).isEqualByComparingTo("6000.00");
        assertThat(response.getDescription()).isEqualTo("Updated Salary");
        // Date unchanged
        assertThat(response.getDate()).isEqualTo(LocalDate.of(2024, 1, 15));
    }

    @Test
    @DisplayName("updateTransaction - category change applied")
    void updateTransaction_categoryChange() {
        Category foodCategory = Category.builder()
                .id(2L).name("Food").type(TransactionType.EXPENSE).custom(false).build();

        UpdateTransactionRequest request = new UpdateTransactionRequest();
        request.setCategory("Food");

        Transaction updated = Transaction.builder()
                .id(1L).amount(new BigDecimal("5000.00"))
                .date(LocalDate.of(2024, 1, 15))
                .category(foodCategory).description("January Salary")
                .user(user).deleted(false).build();

        when(transactionRepository.findByIdAndUserAndDeletedFalse(1L, user))
                .thenReturn(Optional.of(sampleTransaction));
        when(categoryService.resolveCategoryForUser("Food", user)).thenReturn(foodCategory);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(updated);

        TransactionResponse response = transactionService.updateTransaction(1L, request, user);

        assertThat(response.getCategory()).isEqualTo("Food");
    }

    @Test
    @DisplayName("updateTransaction - not found: throws ResourceNotFoundException")
    void updateTransaction_notFound() {
        UpdateTransactionRequest request = new UpdateTransactionRequest();
        when(transactionRepository.findByIdAndUserAndDeletedFalse(99L, user))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.updateTransaction(99L, request, user))
                .isInstanceOf(ResourceNotFoundException.class);
    }

   

    @Test
    @DisplayName("deleteTransaction - success: transaction soft-deleted")
    void deleteTransaction_success() {
        when(transactionRepository.findByIdAndUserAndDeletedFalse(1L, user))
                .thenReturn(Optional.of(sampleTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(sampleTransaction);

        MessageResponse response = transactionService.deleteTransaction(1L, user);

        assertThat(response.getMessage()).isEqualTo("Transaction deleted successfully");
        assertThat(sampleTransaction.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("deleteTransaction - not found: throws ResourceNotFoundException")
    void deleteTransaction_notFound() {
        when(transactionRepository.findByIdAndUserAndDeletedFalse(99L, user))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.deleteTransaction(99L, user))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
