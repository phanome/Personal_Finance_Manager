package com.finance.manager.service;

import com.finance.manager.dto.request.CategoryRequest;
import com.finance.manager.dto.response.CategoryListResponse;
import com.finance.manager.dto.response.CategoryResponse;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.User;
import com.finance.manager.enums.TransactionType;
import com.finance.manager.exception.BadRequestException;
import com.finance.manager.exception.ConflictException;
import com.finance.manager.exception.ForbiddenException;
import com.finance.manager.exception.ResourceNotFoundException;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private TransactionRepository transactionRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User user;
    private Category defaultCategory;
    private Category customCategory;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("test@example.com").build();

        defaultCategory = Category.builder()
                .id(1L).name("Salary").type(TransactionType.INCOME).custom(false).build();

        customCategory = Category.builder()
                .id(2L).name("Freelance").type(TransactionType.INCOME).custom(true).user(user).build();
    }

    
    @Test
    @DisplayName("getAllCategories - returns default + user's custom categories")
    void getAllCategories_returnsBothDefaultAndCustom() {
        when(categoryRepository.findByCustomFalse()).thenReturn(List.of(defaultCategory));
        when(categoryRepository.findByUserAndCustomTrue(user)).thenReturn(List.of(customCategory));

        CategoryListResponse response = categoryService.getAllCategories(user);

        assertThat(response.getCategories()).hasSize(2);
        assertThat(response.getCategories().get(0).getName()).isEqualTo("Salary");
        assertThat(response.getCategories().get(1).getName()).isEqualTo("Freelance");
    }

    

    @Test
    @DisplayName("createCategory - success: new unique custom category created")
    void createCategory_success() {
        CategoryRequest request = new CategoryRequest();
        request.setName("SideIncome");
        request.setType(TransactionType.INCOME);

        when(categoryRepository.existsByNameAndCustomFalse("SideIncome")).thenReturn(false);
        when(categoryRepository.existsByNameAndUser("SideIncome", user)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(
                Category.builder().id(3L).name("SideIncome").type(TransactionType.INCOME).custom(true).user(user).build());

        CategoryResponse response = categoryService.createCategory(request, user);

        assertThat(response.getName()).isEqualTo("SideIncome");
        assertThat(response.isCustom()).isTrue();
    }

    @Test
    @DisplayName("createCategory - conflict with default category")
    void createCategory_conflictWithDefault() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Salary");
        request.setType(TransactionType.INCOME);

        when(categoryRepository.existsByNameAndCustomFalse("Salary")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(request, user))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("createCategory - conflict with existing user custom category")
    void createCategory_conflictWithUserCustom() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Freelance");
        request.setType(TransactionType.INCOME);

        when(categoryRepository.existsByNameAndCustomFalse("Freelance")).thenReturn(false);
        when(categoryRepository.existsByNameAndUser("Freelance", user)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(request, user))
                .isInstanceOf(ConflictException.class);
    }

   
    @Test
    @DisplayName("deleteCategory - success: custom category with no transactions deleted")
    void deleteCategory_success() {
        when(categoryRepository.existsByNameAndCustomFalse("Freelance")).thenReturn(false);
        when(categoryRepository.findByNameAndUser("Freelance", user)).thenReturn(Optional.of(customCategory));
        when(transactionRepository.existsByCategoryAndDeletedFalse(customCategory)).thenReturn(false);

        MessageResponse response = categoryService.deleteCategory("Freelance", user);

        assertThat(response.getMessage()).isEqualTo("Category deleted successfully");
        verify(categoryRepository).delete(customCategory);
    }

    @Test
    @DisplayName("deleteCategory - forbidden: cannot delete default category")
    void deleteCategory_defaultCategory_forbidden() {
        when(categoryRepository.existsByNameAndCustomFalse("Salary")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory("Salary", user))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("deleteCategory - not found: category doesn't exist for user")
    void deleteCategory_notFound() {
        when(categoryRepository.existsByNameAndCustomFalse("Unknown")).thenReturn(false);
        when(categoryRepository.findByNameAndUser("Unknown", user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory("Unknown", user))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deleteCategory - bad request: category referenced by active transactions")
    void deleteCategory_inUse_badRequest() {
        when(categoryRepository.existsByNameAndCustomFalse("Freelance")).thenReturn(false);
        when(categoryRepository.findByNameAndUser("Freelance", user)).thenReturn(Optional.of(customCategory));
        when(transactionRepository.existsByCategoryAndDeletedFalse(customCategory)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory("Freelance", user))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("referenced by existing transactions");
    }

    
    @Test
    @DisplayName("resolveCategoryForUser - resolves default category")
    void resolveCategoryForUser_defaultCategory() {
        when(categoryRepository.findByNameAndCustomFalse("Salary")).thenReturn(Optional.of(defaultCategory));

        Category resolved = categoryService.resolveCategoryForUser("Salary", user);

        assertThat(resolved.getName()).isEqualTo("Salary");
        assertThat(resolved.isCustom()).isFalse();
    }

    @Test
    @DisplayName("resolveCategoryForUser - resolves user's custom category")
    void resolveCategoryForUser_customCategory() {
        when(categoryRepository.findByNameAndCustomFalse("Freelance")).thenReturn(Optional.empty());
        when(categoryRepository.findByNameAndUser("Freelance", user)).thenReturn(Optional.of(customCategory));

        Category resolved = categoryService.resolveCategoryForUser("Freelance", user);

        assertThat(resolved.getName()).isEqualTo("Freelance");
    }

    @Test
    @DisplayName("resolveCategoryForUser - not found throws BadRequestException")
    void resolveCategoryForUser_notFound() {
        when(categoryRepository.findByNameAndCustomFalse("Unknown")).thenReturn(Optional.empty());
        when(categoryRepository.findByNameAndUser("Unknown", user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.resolveCategoryForUser("Unknown", user))
                .isInstanceOf(BadRequestException.class);
    }
}
