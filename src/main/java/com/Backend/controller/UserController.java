package com.Backend.controller;

import com.Backend.model.User;
import com.Backend.repository.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    IUserRepo repo;

    /*
     * Ejemplo de inserción de usuario, se recuerda que todos los atributos son necesarios.
     * localhost:8080/instertarUser?mail=""&masterPassword=""
     * Habrá que personalizar los atributos dandoles una longitud etc...
     * Cuando no todos los atributos sean obligatorios añadir required = false junto al name.
     */
    @CrossOrigin
    @RequestMapping("/insertarUser")
    public String insertarCat ( @RequestParam(name = "mail") String mail,
                                @RequestParam(name = "masterPassword") String mp)
    {
        repo.save(new User(mail,mp));
        return "Insertada categoría de email: " + mail + " y contraseña: " + mp;
    }
}
