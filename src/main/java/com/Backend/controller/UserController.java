package com.Backend.controller;

import com.Backend.exception.UserNotFoundException;
import com.Backend.model.User;
import com.Backend.model.request.UserRegisterRequest;
import com.Backend.model.response.UserResponse;
import com.Backend.repository.IUserRepo;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.Backend.security.Constants.*;
import static com.Backend.utils.TokenUtils.getJWTToken;
import static com.Backend.utils.TokenUtils.getUserIdFromRequest;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.OPTIONS})
public class UserController {

    /* URLs que no son accesibles desde ninguna otra clase */
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
    public ResponseEntity<JSONObject> registro(@RequestBody UserRegisterRequest userRegReq) {
        JSONObject res = new JSONObject();
        if (!userRegReq.isValid()) {

            res.put("statusText", "Faltan campos.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        } else {
            if (!repo.existsByMail(userRegReq.getMail())) {
                userRegReq.setMasterPassword(new BCryptPasswordEncoder().encode(userRegReq.getMasterPassword()));
                repo.save(userRegReq.getAsUser());

                res.put("statusText", "El usuario ha sido insertado.");
                return ResponseEntity.status(HttpStatus.OK).body(res);

            } else {
                res.put("statusText", "El email ya está asociado a una cuenta.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }
        }
    }

    /*
     * Falta adaptar los códigos de respuesta
     */
    @PostMapping(LOGIN_USUARIO_URL)
    public ResponseEntity<JSONObject> login(@RequestBody UserRegisterRequest userRegReq) {
        JSONObject res = new JSONObject();
        if (userRegReq.isValid() && repo.existsByMail(userRegReq.getMail())) {

            User recuperado = repo.findByMail(userRegReq.getMail());
            BCryptPasswordEncoder b = new BCryptPasswordEncoder();

            if (b.matches(userRegReq.getMasterPassword(), recuperado.getMasterPassword())) {
                String token = getJWTToken(recuperado);
                res.put("statusText", "Sesión iniciada.");
                res.put("token", token);
                return ResponseEntity.status(HttpStatus.OK).body(res);
            } else {
                res.put("statusText", "Credenciales incorrectos");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }
        } else {
            res.put("statusText", "Credenciales incorrectos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    /*
     *
     */
    @GetMapping(TOKEN_USUARIO_URL)
    public ResponseEntity<JSONObject> login(HttpServletRequest request) throws UserNotFoundException {
        Long id = getUserIdFromRequest(request);
        JSONObject res = new JSONObject();
        if (repo.existsById(id)){
            User usuario = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
            String token = getJWTToken(usuario);

            res.put("statusText", "OK");
            res.put("token", token);
            return ResponseEntity.status(HttpStatus.OK).body(res);

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /*
     * Devuelve si existe un JSON con la info del usuario, en caso contrario lanza excepcion
     */
    @GetMapping(CONSULTAR_USUARIO_URL)
    public ResponseEntity<JSONObject>  consulta(HttpServletRequest request) throws UserNotFoundException {
        Long id = getUserIdFromRequest(request);
        JSONObject res = new JSONObject();
        if (repo.existsById(id)){
            User usuario = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
            res.put("statusText", "OK");
            res.put("user", new UserResponse(usuario));
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        else{

            res.put("statusText", "UNAUTHORIZED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }
    }


    /*
     * Método para eliminar usuario
     */
    @DeleteMapping(ELIMINAR_USUARIO_URL)
    public ResponseEntity<JSONObject> eliminar(HttpServletRequest request) {
        Long id = getUserIdFromRequest(request);
        JSONObject res = new JSONObject();
        if (repo.existsById(id)) {
            repo.deleteById(id);
            res.put("statusText", "Usuario eliminado");
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        else{
            res.put("statusText", "No autorizado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
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
    public List<UserResponse> all() {
        List<User> listaUsers = repo.findAll();
        List<UserResponse> listaRespuesta = new ArrayList<>();

        for(User u : listaUsers)
            listaRespuesta.add(new UserResponse(u));

        return listaRespuesta;
    }
}
