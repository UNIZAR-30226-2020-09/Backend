package com.Backend.repository;

import com.Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepo extends JpaRepository<User, Long>{
    User findByMail(String mail);

    //Sería conveniente añadir un índice a los mails para acelerar las consultas.
}
