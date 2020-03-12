package com.Backend.controller;

import com.Backend.exception.UserNotFoundException;
import com.Backend.model.User;
import com.Backend.model.request.UserRegisterRequest;
import com.Backend.repository.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    IUserRepo repo;

    /*
     * Ejemplo de inserción de usuario, se recuerda que todos los atributos son necesarios.
     * Habrá que personalizar los atributos dandoles una longitud etc...
     * Cuando no todos los atributos sean obligatorios añadir required = false junto al name.
     * se puede hacer que devuelva algún JSON que confirme o deniegue la correcta inserción
     */
    @CrossOrigin
    @PostMapping("/api/users/registro")
    public ResponseEntity<String> registro (@RequestBody UserRegisterRequest userRegReq) {
        if (!userRegReq.isValid()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Faltan campos.");
        }
        else {
            if (!repo.existsByMail(userRegReq.getMail())) {
                repo.save(userRegReq.getAsUser());
                return ResponseEntity.status(HttpStatus.OK).body("El usuario ha sido insertado.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El email ya está asociado a una cuenta.");
            }
        }
    }

    /*
     * Devuelve si existe un JSON con la info del usuario, en caso contrario lanza excepcion
     */
    @CrossOrigin
    @GetMapping("api/users/consultar/{id}")
    public User consulta(@PathVariable Long id) throws UserNotFoundException {
        User usuario = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return usuario;
    }

    /*
     * Devuelve la información de todos los usuarios de la base de datos
     */
    @CrossOrigin
    @GetMapping("api/users/consultar")
    public List<User> all() {
        return repo.findAll();
    }


    /*
     * Método para eliminar usuarios, en proceso de debug, deberíamos usar DELETE
     */
    @CrossOrigin
    @DeleteMapping("api/users/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) throws UserNotFoundException {
        User usuario = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        repo.deleteById(usuario.getId());
        return ResponseEntity.status(HttpStatus.OK).body("El usuario ha sido eliminado");
    }
}
