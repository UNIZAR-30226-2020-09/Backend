package com.Backend.repository;
import java.util.List;

import com.Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUserName(String userName);
    User findById(long id);
}