package com.finance.manager.service;

import com.finance.manager.dto.request.CategoryRequest;
import com.finance.manager.dto.response.CategoryListResponse;
import com.finance.manager.dto.response.CategoryResponse;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.User;
import com.finance.manager.exception.BadRequestException;
import com.finance.manager.exception.ConflictException;
import com.finance.manager.exception.ForbiddenException;
import com.finance.manager.exception.ResourceNotFoundException;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for category management operations.
 */
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Returns all categories accessible to a user — default categories plus the user's own custom ones.
     *
     * @param user the authenticated user
     */
    @Transactional(readOnly = true)
    public CategoryListResponse getAllCategories(User user) {
        List<CategoryResponse> responses = new ArrayList<>();

        // Default (system) categories
        categoryRepository.findByCustomFalse().forEach(c ->
                responses.add(mapToResponse(c)));

        // User's custom categories
        categoryRepository.findByUserAndCustomTrue(user).forEach(c ->
                responses.add(mapToResponse(c)));

        return new CategoryListResponse(responses);
    }

    /**
     * Creates a new custom category for the user.
     *
     * @param request category creation payload
     * @param user    the category owner
     * @return the created category
     * @throws ConflictException if a category with that name already exists for the user or as a default
     */
    public CategoryResponse createCategory(CategoryRequest request, User user) {
        String name = request.getName().trim();

        // Check against default categories
        if (categoryRepository.existsByNameAndCustomFalse(name)) {
            throw new ConflictException("A default category named '" + name + "' already exists");
        }

        // Check against user's existing custom categories
        if (categoryRepository.existsByNameAndUser(name, user)) {
            throw new ConflictException("You already have a custom category named '" + name + "'");
        }

        Category category = Category.builder()
                .name(name)
                .type(request.getType())
                .custom(true)
                .user(user)
                .build();

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    /**
     * Deletes a custom category by name.
     *
     * @param name the category name
     * @param user the requesting user
     * @return success message
     * @throws ForbiddenException       if the category is a default (non-custom) category
     * @throws ResourceNotFoundException if the category does not exist for the user
     * @throws BadRequestException       if the category is referenced by active transactions
     */
    public MessageResponse deleteCategory(String name, User user) {
        // Check if it's a default category — cannot delete
        if (categoryRepository.existsByNameAndCustomFalse(name)) {
            throw new ForbiddenException("Default categories cannot be deleted");
        }

        Category category = categoryRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category '" + name + "' not found"));

        // Check if any non-deleted transactions reference this category
        if (transactionRepository.existsByCategoryAndDeletedFalse(category)) {
            throw new BadRequestException(
                    "Cannot delete category '" + name + "' because it is referenced by existing transactions");
        }

        categoryRepository.delete(category);
        return new MessageResponse("Category deleted successfully");
    }

    // -----------------------------------------------------------------------
    // Package-private helpers used by other services
    // -----------------------------------------------------------------------

    /**
     * Resolves a category by name for a given user. Checks default categories first,
     * then the user's custom categories.
     *
     * @param categoryName the requested category name
     * @param user         the requesting user
     * @return the matching {@link Category}
     * @throws BadRequestException if no accessible category with that name exists
     */
    public Category resolveCategoryForUser(String categoryName, User user) {
        // First try default categories
        Category cat = categoryRepository.findByNameAndCustomFalse(categoryName).orElse(null);
        if (cat != null) {
            return cat;
        }

        // Then try the user's custom categories
        return categoryRepository.findByNameAndUser(categoryName, user)
                .orElseThrow(() -> new BadRequestException(
                        "Category '" + categoryName + "' not found. " +
                        "Use an existing default or your own custom category."));
    }

    // -----------------------------------------------------------------------
    // Mappers
    // -----------------------------------------------------------------------

    /**
     * Converts a {@link Category} entity to a {@link CategoryResponse} DTO.
     */
    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(
                category.getName(),
                category.getType().name(),
                category.isCustom());
    }
}
