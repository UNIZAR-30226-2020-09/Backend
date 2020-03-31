package com.Backend.repository;

import com.Backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICatRepo extends JpaRepository<Category, Long> {
    Boolean existsByUsuarioAndCategoryName(User user, String categoryName);
    List<Category> findByUsuario(User user);
    List<Category> findByUsuarioOrderByIdAsc(User user);
    Boolean existsByIdAndUsuario(Long id, User user);
    Category findByUsuarioAndId(User user, Long id);
}