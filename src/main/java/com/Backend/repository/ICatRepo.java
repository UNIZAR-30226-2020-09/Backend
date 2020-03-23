package com.Backend.repository;

import com.Backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICatRepo extends JpaRepository<Category, Long> {
    Category findByUserAndCategoryName(User user, String categoryName);
}