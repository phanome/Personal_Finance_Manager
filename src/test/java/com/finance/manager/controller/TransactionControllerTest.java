package com.finance.manager.controller;

import com.finance.manager.dto.request.TransactionRequest;
import com.finance.manager.dto.request.UpdateTransactionRequest;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.dto.response.TransactionListResponse;
import com.finance.manager.dto.response.TransactionResponse;
import com.finance.manager.entity.User;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock private TransactionService transactionService;
    @InjectMocks private TransactionController transactionController;

    private CustomUserDetails userDetails;
    private TransactionResponse sampleResponse;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).username("test@test.com").password("pass").fullName("Test").build();
        userDetails = new CustomUserDetails(user);

        sampleResponse = new TransactionResponse(1L, new BigDecimal("5000.00"),
                "January Salary", LocalDate.of(2024, 1, 15), "INCOME", "Salary");
    }

    @Test
    @DisplayName("POST /api/transactions → 201 Created")
    void createTransaction() {
        TransactionRequest req = new TransactionRequest();
        req.setAmount(new BigDecimal("5000.00"));
        req.setDate(LocalDate.of(2024, 1, 15));
        req.setCategory("Salary");
        req.setDescription("January Salary");

        when(transactionService.createTransaction(any(), any())).thenReturn(sampleResponse);

        ResponseEntity<TransactionResponse> response = transactionController.createTransaction(req, userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getAmount()).isEqualByComparingTo("5000.00");
    }

    @Test
    @DisplayName("GET /api/transactions → 200 OK with list")
    void getTransactions() {
        when(transactionService.getTransactions(any(), any(), any(), any(), any()))
                .thenReturn(new TransactionListResponse(List.of(sampleResponse)));

        ResponseEntity<TransactionListResponse> response =
                transactionController.getTransactions(null, null, null, null, userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTransactions()).hasSize(1);
    }

    @Test
    @DisplayName("PUT /api/transactions/1 → 200 OK")
    void updateTransaction() {
        UpdateTransactionRequest req = new UpdateTransactionRequest();
        req.setAmount(new BigDecimal("6000.00"));

        TransactionResponse updated = new TransactionResponse(1L, new BigDecimal("6000.00"),
                "Updated Salary", LocalDate.of(2024, 1, 15), "INCOME", "Salary");

        when(transactionService.updateTransaction(eq(1L), any(), any())).thenReturn(updated);

        ResponseEntity<TransactionResponse> response =
                transactionController.updateTransaction(1L, req, userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getAmount()).isEqualByComparingTo("6000.00");
    }

    @Test
    @DisplayName("DELETE /api/transactions/1 → 200 OK")
    void deleteTransaction() {
        when(transactionService.deleteTransaction(eq(1L), any()))
                .thenReturn(new MessageResponse("Transaction deleted successfully"));

        ResponseEntity<MessageResponse> response = transactionController.deleteTransaction(1L, userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("Transaction deleted successfully");
    }
}
