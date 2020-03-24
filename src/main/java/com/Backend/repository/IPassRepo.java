package com.Backend.repository;

import com.Backend.model.Password;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPassRepo extends JpaRepository<Password, Long>{
}
