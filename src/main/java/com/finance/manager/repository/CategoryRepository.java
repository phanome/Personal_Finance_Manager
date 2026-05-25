package com.finance.manager.repository;

import com.finance.manager.entity.Category;
import com.finance.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    
    List<Category> findByCustomFalse();

    
    List<Category> findByUserAndCustomTrue(User user);

    
    Optional<Category> findByNameAndCustomFalse(String name);

    
    Optional<Category> findByNameAndUser(String name, User user);

    
    boolean existsByNameAndUser(String name, User user);

    
    boolean existsByNameAndCustomFalse(String name);
}
