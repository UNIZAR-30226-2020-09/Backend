package com.Backend.repository;

import com.Backend.model.OwnsPassword;
import com.Backend.model.OwnsPasswordKey;
import com.Backend.model.Password;
import com.Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface IOwnsPassRepo extends JpaRepository<OwnsPassword, Long>{
    OwnsPassword findByPasswordAndUser(Password password, User user);
    List<OwnsPassword> findAllByUser(User user);

    @Transactional
    @Modifying
    @Query("delete FROM OwnsPassword p WHERE p.key.passwordId = :passId AND p.rol <> :rol")
    int deleteByPasswordKeyAndRolNot(@Param("passId") Long passwordId, @Param("rol") int rol);

    boolean existsByPasswordAndUserAndGrupo(Password password, User user, int grupo);
}
