package com.Backend.repository;

import com.Backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICatRepo extends JpaRepository<Category, Long> {
}