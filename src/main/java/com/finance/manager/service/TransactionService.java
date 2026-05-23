package com.finance.manager.service;

import com.finance.manager.dto.request.TransactionRequest;
import com.finance.manager.dto.request.UpdateTransactionRequest;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.dto.response.TransactionListResponse;
import com.finance.manager.dto.response.TransactionResponse;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.exception.ResourceNotFoundException;
import com.finance.manager.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for financial transaction CRUD operations.
 */
@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    public TransactionService(TransactionRepository transactionRepository,
                              CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
    }

    /**
     * Creates a new transaction for the authenticated user.
     *
     * @param request the transaction details
     * @param user    the owning user
     * @return the created transaction
     */
    public TransactionResponse createTransaction(TransactionRequest request, User user) {
        Category category = categoryService.resolveCategoryForUser(request.getCategory(), user);

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .date(request.getDate())
                .category(category)
                .description(request.getDescription())
                .user(user)
                .deleted(false)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    /**
     * Retrieves all non-deleted transactions for the user, with optional filters.
     *
     * @param user       the authenticated user
     * @param startDate  optional start date filter (inclusive)
     * @param endDate    optional end date filter (inclusive)
     * @param categoryId optional category ID filter
     * @return list of matching transactions ordered newest first
     */
    @Transactional(readOnly = true)
    public TransactionListResponse getTransactions(User user,
                                                    LocalDate startDate,
                                                    LocalDate endDate,
                                                    Long categoryId) {
        List<TransactionResponse> list = transactionRepository
                .findByFilters(user, startDate, endDate, categoryId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return new TransactionListResponse(list);
    }

    /**
     * Updates a transaction's amount, category, and/or description.
     * The transaction date cannot be changed.
     *
     * @param id      the transaction id
     * @param request fields to update
     * @param user    the authenticated user
     * @return the updated transaction
     * @throws ResourceNotFoundException if the transaction doesn't exist or belongs to another user
     */
    public TransactionResponse updateTransaction(Long id, UpdateTransactionRequest request, User user) {
        Transaction transaction = transactionRepository.findByIdAndUserAndDeletedFalse(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        if (request.getCategory() != null) {
            Category category = categoryService.resolveCategoryForUser(request.getCategory(), user);
            transaction.setCategory(category);
        }
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    /**
     * Soft-deletes a transaction so it is excluded from reports and savings goals.
     *
     * @param id   the transaction id
     * @param user the authenticated user
     * @return success message
     * @throws ResourceNotFoundException if the transaction doesn't exist or belongs to another user
     */
    public MessageResponse deleteTransaction(Long id, User user) {
        Transaction transaction = transactionRepository.findByIdAndUserAndDeletedFalse(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        transaction.setDeleted(true);
        transactionRepository.save(transaction);
        return new MessageResponse("Transaction deleted successfully");
    }

    // -----------------------------------------------------------------------
    // Mapper
    // -----------------------------------------------------------------------

    /**
     * Converts a {@link Transaction} entity to a {@link TransactionResponse} DTO.
     */
    private TransactionResponse mapToResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getAmount(),
                t.getDescription(),
                t.getDate(),
                t.getCategory().getType().name(),
                t.getCategory().getName());
    }
}
