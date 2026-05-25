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

    
    @Transactional(readOnly = true)
    public CategoryListResponse getAllCategories(User user) {
        List<CategoryResponse> responses = new ArrayList<>();

       
        categoryRepository.findByCustomFalse().forEach(c ->
                responses.add(mapToResponse(c)));

        
        categoryRepository.findByUserAndCustomTrue(user).forEach(c ->
                responses.add(mapToResponse(c)));

        return new CategoryListResponse(responses);
    }

    
    public CategoryResponse createCategory(CategoryRequest request, User user) {
        String name = request.getName().trim();

       
        if (categoryRepository.existsByNameAndCustomFalse(name)) {
            throw new ConflictException("A default category named '" + name + "' already exists");
        }

       
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

   
    public MessageResponse deleteCategory(String name, User user) {
       
        if (categoryRepository.existsByNameAndCustomFalse(name)) {
            throw new ForbiddenException("Default categories cannot be deleted");
        }

        Category category = categoryRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category '" + name + "' not found"));

      
        if (transactionRepository.existsByCategoryAndDeletedFalse(category)) {
            throw new BadRequestException(
                    "Cannot delete category '" + name + "' because it is referenced by existing transactions");
        }

        categoryRepository.delete(category);
        return new MessageResponse("Category deleted successfully");
    }

    
    public Category resolveCategoryForUser(String categoryName, User user) {
        
        Category cat = categoryRepository.findByNameAndCustomFalse(categoryName).orElse(null);
        if (cat != null) {
            return cat;
        }

       
        return categoryRepository.findByNameAndUser(categoryName, user)
                .orElseThrow(() -> new BadRequestException(
                        "Category '" + categoryName + "' not found. " +
                        "Use an existing default or your own custom category."));
    }

    
    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(
                category.getName(),
                category.getType().name(),
                category.isCustom());
    }
}
