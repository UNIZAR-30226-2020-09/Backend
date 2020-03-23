package com.Backend.repository;

import com.Backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICatRepo extends JpaRepository<Category, Long> {
    Boolean existsByUsuarioAndCategoryName(User user, String categoryName);
}