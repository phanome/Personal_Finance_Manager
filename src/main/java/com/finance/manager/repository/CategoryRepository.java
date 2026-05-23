package com.finance.manager.repository;

import com.finance.manager.entity.Category;
import com.finance.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Category} entity operations.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Returns all system-wide default categories (non-custom).
     */
    List<Category> findByCustomFalse();

    /**
     * Returns all custom categories belonging to the given user.
     *
     * @param user the category owner
     */
    List<Category> findByUserAndCustomTrue(User user);

    /**
     * Finds a default (non-custom) category by name.
     *
     * @param name the category name
     */
    Optional<Category> findByNameAndCustomFalse(String name);

    /**
     * Finds a custom category by name and owning user.
     *
     * @param name the category name
     * @param user the owning user
     */
    Optional<Category> findByNameAndUser(String name, User user);

    /**
     * Checks whether a custom category with the given name exists for a user.
     *
     * @param name the category name
     * @param user the owning user
     */
    boolean existsByNameAndUser(String name, User user);

    /**
     * Checks whether a default category with the given name exists.
     *
     * @param name the category name
     */
    boolean existsByNameAndCustomFalse(String name);
}
