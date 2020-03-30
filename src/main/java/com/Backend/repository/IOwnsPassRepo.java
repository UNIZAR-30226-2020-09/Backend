package com.Backend.repository;

import com.Backend.model.OwnsPassword;
import com.Backend.model.Password;
import com.Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOwnsPassRepo extends JpaRepository<OwnsPassword, Long>{
    OwnsPassword findByPasswordAndUser(Password password, User user);
    List<OwnsPassword> findAllByUser(User user);
    List<OwnsPassword> findAllByPassword(Password password);
}