package com.Backend.repository;

import com.Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepo extends JpaRepository<User, Long>{
    boolean existsByMail(String mail);
    User findByMail(String username);
}
