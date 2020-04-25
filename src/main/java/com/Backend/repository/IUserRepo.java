package com.Backend.repository;

import com.Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepo extends JpaRepository<User, Long>{
    boolean existsByMail(String mail);
    User findByMail(String username);
}
