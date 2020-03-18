package com.Backend.controller;

import com.Backend.exception.UserNotFoundException;
import com.Backend.model.User;
import com.Backend.model.request.UserRegisterRequest;
import com.Backend.repository.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.Backend.security.Constants.*;
import static com.Backend.utils.TokenUtils.*;

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
    @PostMapping(REGISTRO_URL)
    public ResponseEntity<String> registro(@RequestBody UserRegisterRequest userRegReq) {
        if (!userRegReq.isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Faltan campos.");
        } else {
            if (!repo.existsByMail(userRegReq.getMail())) {
                userRegReq.setMasterPassword(new BCryptPasswordEncoder().encode(userRegReq.getMasterPassword()));
                repo.save(userRegReq.getAsUser());
                return ResponseEntity.status(HttpStatus.OK).body("El usuario ha sido insertado.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El email ya está asociado a una cuenta.");
            }
        }
    }

    /*
     * Falta adaptar los códigos de respuesta
     */
    @PostMapping(LOGIN_URL)
    public ResponseEntity<User> login(@RequestBody UserRegisterRequest userRegReq) {
        if (userRegReq.isValid() && repo.existsByMail(userRegReq.getMail())) {

            User recuperado = repo.findByMail(userRegReq.getMail());
            BCryptPasswordEncoder b = new BCryptPasswordEncoder();

            if (b.matches(userRegReq.getMasterPassword(), recuperado.getMasterPassword())) {
                String token = getJWTToken(recuperado);
                User user = new User();
                user.setMasterPassword(userRegReq.getMasterPassword());
                user.setToken(token);
                return ResponseEntity.status(HttpStatus.OK).body(user);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userRegReq.getAsUser());
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userRegReq.getAsUser());
        }
    }

    /*
     * Devuelve si existe un JSON con la info del usuario, en caso contrario lanza excepcion
     */
    @GetMapping(CONSULTAR_PROPIA_INFO)
    public User consulta(HttpServletRequest request) throws UserNotFoundException {
        Long id = getUserFromRequest(request);
        User usuario = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return usuario;
    }

    /*
     * Devuelve la información de todos los usuarios de la base de datos
     */
    @GetMapping(LISTAR_TODOS_URL)
    public List<User> all() {
        return repo.findAll();
    }


    /*
     * Método para eliminar usuarios, en proceso de debug, deberíamos usar DELETE
     * ADAPTAR AL TOKEN
     */
    /*@DeleteMapping("api/users/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) throws UserNotFoundException {
        User usuario = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        repo.deleteById(usuario.getId());
        return ResponseEntity.status(HttpStatus.OK).body("El usuario ha sido eliminado");
    }*/
}
