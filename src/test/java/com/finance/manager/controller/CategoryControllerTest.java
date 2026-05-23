package com.finance.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.manager.dto.request.CategoryRequest;
import com.finance.manager.dto.response.CategoryListResponse;
import com.finance.manager.dto.response.CategoryResponse;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.entity.User;
import com.finance.manager.enums.TransactionType;
import com.finance.manager.security.CustomUserDetails;
import com.finance.manager.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CategoryController}.
 */
@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock private CategoryService categoryService;
    @InjectMocks private CategoryController categoryController;

    private CustomUserDetails userDetails;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("test@test.com").password("pass").fullName("Test").build();
        userDetails = new CustomUserDetails(user);
    }

    @Test
    @DisplayName("GET /api/categories → 200 OK with category list")
    void getAllCategories() {
        CategoryResponse cat = new CategoryResponse("Salary", "INCOME", false);
        when(categoryService.getAllCategories(any()))
                .thenReturn(new CategoryListResponse(List.of(cat)));

        ResponseEntity<CategoryListResponse> response = categoryController.getAllCategories(userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCategories()).hasSize(1);
        assertThat(response.getBody().getCategories().get(0).getName()).isEqualTo("Salary");
    }

    @Test
    @DisplayName("POST /api/categories → 201 Created")
    void createCategory() {
        CategoryRequest req = new CategoryRequest();
        req.setName("Freelance");
        req.setType(TransactionType.INCOME);

        CategoryResponse resp = new CategoryResponse("Freelance", "INCOME", true);
        when(categoryService.createCategory(any(), any())).thenReturn(resp);

        ResponseEntity<CategoryResponse> response = categoryController.createCategory(req, userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getName()).isEqualTo("Freelance");
        assertThat(response.getBody().isCustom()).isTrue();
    }

    @Test
    @DisplayName("DELETE /api/categories/Freelance → 200 OK")
    void deleteCategory() {
        when(categoryService.deleteCategory(eq("Freelance"), any()))
                .thenReturn(new MessageResponse("Category deleted successfully"));

        ResponseEntity<MessageResponse> response = categoryController.deleteCategory("Freelance", userDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("Category deleted successfully");
    }
}
