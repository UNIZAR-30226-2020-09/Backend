package com.Backend.controller;

import com.Backend.exception.UserNotFoundException;
import com.Backend.model.User;
import com.Backend.repository.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.CollectionModel;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    IUserRepo repo;

    /*
     * Ejemplo de inserción de usuario, se recuerda que todos los atributos son necesarios.
     * localhost:8080/instertarUser?mail=""&masterPassword=""
     * Habrá que personalizar los atributos dandoles una longitud etc...
     * Cuando no todos los atributos sean obligatorios añadir required = false junto al name.
     * se puede hacer que devuelva algún JSON que confirme o deniegue la correcta inserción
     */
    @CrossOrigin
    @GetMapping("/users/insertar")
    public void insertaUser (@RequestParam(name = "mail") String mail,
                             @RequestParam(name = "masterPassword") String mp)
    {
        User usuario = new User(mail,mp);
        repo.save(usuario);
    }

    /*
     * Devuelve si existe un JSON con la info del usuario, en caso contrario lanza excepcion
     */
    @CrossOrigin
    @GetMapping("/users/{id}")
    public EntityModel<User> consulta(@PathVariable Long id) throws UserNotFoundException {
        User usuario = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        return new EntityModel<>(usuario,
                linkTo(methodOn(UserController.class).consulta(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).all()).withRel("usuarios"));
    }

    /*
     * Devuelve la información de todos los usuarios de la base de datos
     */
    @CrossOrigin
    @GetMapping("/users")
    public CollectionModel<EntityModel<User>> all() {
        List<EntityModel<User>> usuarios = repo.findAll().stream()
                .map(usuario -> {
                    try {
                        return new EntityModel<>(usuario,
                                linkTo(methodOn(UserController.class).consulta(usuario.getId())).withSelfRel(),
                                linkTo(methodOn(UserController.class).all()).withRel("usuarios"));
                    } catch (UserNotFoundException e) {
                        return new EntityModel<>(usuario,
                                linkTo(methodOn(UserController.class).all()).withRel("usuarios"));
                    }
                })
                .collect(Collectors.toList());

        return new CollectionModel<>(usuarios,
                linkTo(methodOn(UserController.class).all()).withSelfRel());
    }


    /*
     * Método para eliminar usuarios, en proceso de debug, deberíamos usar DELETE
     */
    @CrossOrigin
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) throws UserNotFoundException {
        User usuario = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        repo.deleteById(usuario.getId());
        return "Se ha eliminado el usuario: " + id;
                //EntityModel<>(usuario, linkTo(methodOn(UserController.class).all()).withRel("usuarios"));
    }
}
