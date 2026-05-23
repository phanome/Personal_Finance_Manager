package com.finance.manager.config;

import com.finance.manager.entity.Category;
import com.finance.manager.enums.TransactionType;
import com.finance.manager.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the database with the default (system-wide) categories on application startup.
 * Default categories cannot be deleted or modified by users.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public DataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @SuppressWarnings("null")
    public void run(String... args) {
        // Only seed if no default categories exist yet
        if (categoryRepository.findByCustomFalse().isEmpty()) {
            List<Category> defaults = List.of(
                // Income
                Category.builder().name("Salary").type(TransactionType.INCOME).custom(false).build(),
                // Expenses
                Category.builder().name("Food").type(TransactionType.EXPENSE).custom(false).build(),
                Category.builder().name("Rent").type(TransactionType.EXPENSE).custom(false).build(),
                Category.builder().name("Transportation").type(TransactionType.EXPENSE).custom(false).build(),
                Category.builder().name("Entertainment").type(TransactionType.EXPENSE).custom(false).build(),
                Category.builder().name("Healthcare").type(TransactionType.EXPENSE).custom(false).build(),
                Category.builder().name("Utilities").type(TransactionType.EXPENSE).custom(false).build()
            );
            categoryRepository.saveAll(defaults);
        }
    }
}
