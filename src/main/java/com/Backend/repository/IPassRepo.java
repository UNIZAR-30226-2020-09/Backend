package com.Backend.repository;

import com.Backend.model.Category;
import com.Backend.model.Password;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IPassRepo extends JpaRepository<Password, Long>{
    List<Password> findByCategory(Category category);
}
