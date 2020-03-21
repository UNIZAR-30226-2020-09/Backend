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

    public static final String LOGOUT_USUARIO_URL = "api/usuarios/logout";
    public static final String TOKEN_USUARIO_URL = "/api/usuarios/token";
    public static final String CONSULTAR_USUARIO_URL =  "/api/usuarios/consultar";
    public static final String ELIMINAR_USUARIO_URL = "/api/usuarios/eliminar";

    @Autowired
    IUserRepo repo;

    /*
     * Ejemplo de inserción de usuario, se recuerda que todos los atributos son necesarios.
     * Habrá que personalizar los atributos dandoles una longitud etc...
     * Cuando no todos los atributos sean obligatorios añadir required = false junto al name.
     * se puede hacer que devuelva algún JSON que confirme o deniegue la correcta inserción
     */
    @PostMapping(REGISTRO_USUARIO_URL)
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
    @PostMapping(LOGIN_USUARIO_URL)
    public ResponseEntity<String> login(@RequestBody UserRegisterRequest userRegReq) {
        if (userRegReq.isValid() && repo.existsByMail(userRegReq.getMail())) {

            User recuperado = repo.findByMail(userRegReq.getMail());
            BCryptPasswordEncoder b = new BCryptPasswordEncoder();

            if (b.matches(userRegReq.getMasterPassword(), recuperado.getMasterPassword())) {
                String token = getJWTToken(recuperado);
                return ResponseEntity.status(HttpStatus.OK).body(token);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /*
     *
     */
    @GetMapping(TOKEN_USUARIO_URL)
    public ResponseEntity<String> login(HttpServletRequest request) throws UserNotFoundException {
        Long id = getUserIdFromRequest(request);
        if (id != null && repo.existsById(id)){
            User usuario = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
            String token = getJWTToken(usuario);
                return ResponseEntity.status(HttpStatus.OK).body(token);

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /*
     * Devuelve si existe un JSON con la info del usuario, en caso contrario lanza excepcion
     */
    @GetMapping(CONSULTAR_USUARIO_URL)
    public ResponseEntity<User>  consulta(HttpServletRequest request) throws UserNotFoundException {
        Long id = getUserIdFromRequest(request);
        if (id != null && repo.existsById(id)){
            User usuario = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
            return ResponseEntity.status(HttpStatus.OK).body(usuario);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


    /*
     * Método para eliminar usuario
     */
    @DeleteMapping(ELIMINAR_USUARIO_URL)
    public ResponseEntity<String> eliminar(HttpServletRequest request) {
        Long id = getUserIdFromRequest(request);
        if (id != null) {
            if (repo.existsById(id)) {
                repo.deleteById(id);
                return ResponseEntity.status(HttpStatus.OK).body("El usuario ha sido eliminado");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuario ya no existe");
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sesión expirada");
        }
    }

    /*
     * Método para logout
     */



    /*
     * Devuelve la información de todos los usuarios de la base de datos
     * SOLO CON PROPOSITO DE DEBUG
     */
    @GetMapping(CONSULTAR_TODOS_USUARIOS_URL)
    public List<User> all() {
        return repo.findAll();
    }
}
