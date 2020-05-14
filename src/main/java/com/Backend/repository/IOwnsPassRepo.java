package com.Backend.repository;

import com.Backend.model.OwnsPassword;
import com.Backend.model.Password;
import com.Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface IOwnsPassRepo extends JpaRepository<OwnsPassword, Long>{
    OwnsPassword findByPasswordAndUser(Password password, User user);
    List<OwnsPassword> findAllByUser(User user);
    List<Password> findAllPasswordsByUserAndRol(User user, int rol);

    @Query("SELECT r.user FROM OwnsPassword r WHERE r.password = :password AND r.rol = :rol")
    List<User> findAllUsersByPasswordAndRol(@Param("password") Password password, @Param("rol") int rol);
    
    List<OwnsPassword> findAllByUserAndRol(User user, int rol);
    Long countByPassword(Password password);
    @Transactional
    void deleteByPasswordAndRol(Password password, int rol);
    boolean existsByPasswordAndUser(Password password, User user);
}
