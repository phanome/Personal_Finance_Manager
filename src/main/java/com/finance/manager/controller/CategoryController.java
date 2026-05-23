package com.finance.manager.controller;

import com.finance.manager.dto.request.CategoryRequest;
import com.finance.manager.dto.response.CategoryListResponse;
import com.finance.manager.dto.response.CategoryResponse;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.entity.User;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for category management.
 * All endpoints require an authenticated session.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Returns all categories accessible to the authenticated user
     * (system defaults + user's custom categories).
     *
     * @param userDetails the authenticated user principal
     * @return 200 OK with category list
     */
    @GetMapping
    public ResponseEntity<CategoryListResponse> getAllCategories(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        return ResponseEntity.ok(categoryService.getAllCategories(user));
    }

    /**
     * Creates a new custom category for the authenticated user.
     *
     * @param request     the category name and type
     * @param userDetails the authenticated user principal
     * @return 201 Created with the new category, or 409 if name already exists
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        CategoryResponse response = categoryService.createCategory(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Deletes a custom category by name.
     *
     * @param name        the category name to delete
     * @param userDetails the authenticated user principal
     * @return 200 OK on success, 400 if in use, 403 if default, 404 if not found
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<MessageResponse> deleteCategory(
            @PathVariable String name,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        MessageResponse response = categoryService.deleteCategory(name, user);
        return ResponseEntity.ok(response);
    }
}
