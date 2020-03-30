package com.Backend.controller;

import com.Backend.exception.UserNotFoundException;
import com.Backend.model.Category;
import com.Backend.model.User;
import com.Backend.model.request.UserRegisterRequest;
import com.Backend.model.response.UserResponse;
import com.Backend.repository.ICatRepo;
import com.Backend.repository.IUserRepo;
import net.minidev.json.JSONArray;
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
    @Autowired
    ICatRepo repoCat;

    @PostMapping(REGISTRO_USUARIO_URL)
    public ResponseEntity<JSONObject> registro(@RequestBody UserRegisterRequest userRegReq) {
        JSONObject res = new JSONObject();

        if (!userRegReq.isValid()) {
            res.put("statusText", "Los campos no pueden queda vacíos.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        } else {
            if (!repo.existsByMail(userRegReq.getMail())) {

                userRegReq.setMasterPassword(new BCryptPasswordEncoder().encode(userRegReq.getMasterPassword()));
                repo.save(userRegReq.getAsUser());
                // Si no buscas un usuario con id falla la inserción.
                User usuario = repo.findByMail(userRegReq.getMail());
                repoCat.save(new Category("Sin categoría", usuario));
                return ResponseEntity.status(HttpStatus.OK).body(res);

            } else {
                res.put("statusText", "El email ya está asociado a una cuenta.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }
        }
    }

    @PostMapping(LOGIN_USUARIO_URL)
    public ResponseEntity<JSONObject> login(@RequestBody UserRegisterRequest userRegReq) {
        JSONObject res = new JSONObject();
        if (userRegReq.isValid() && repo.existsByMail(userRegReq.getMail())) {

            User recuperado = repo.findByMail(userRegReq.getMail());
            BCryptPasswordEncoder b = new BCryptPasswordEncoder();

            if (b.matches(userRegReq.getMasterPassword(), recuperado.getMasterPassword())) {
                String token = getJWTToken(recuperado);
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

    @GetMapping(TOKEN_USUARIO_URL)
    public ResponseEntity<JSONObject> login(HttpServletRequest request) throws UserNotFoundException {
        Long id = getUserIdFromRequest(request);
        JSONObject res = new JSONObject();
        if (repo.existsById(id)){
            User usuario = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
            String token = getJWTToken(usuario);
            res.put("token", token);
            return ResponseEntity.status(HttpStatus.OK).body(res);

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping(CONSULTAR_USUARIO_URL)
    public ResponseEntity<JSONObject>  consulta(HttpServletRequest request) throws UserNotFoundException {
        Long id = getUserIdFromRequest(request);
        JSONObject res = new JSONObject();
        if (repo.existsById(id)){
            User usuario = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
            res.put("user", new UserResponse(usuario));
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        else{

            res.put("statusText", "UNAUTHORIZED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    @DeleteMapping(ELIMINAR_USUARIO_URL)
    public ResponseEntity<JSONObject> eliminar(HttpServletRequest request) {
        Long id = getUserIdFromRequest(request);
        JSONObject res = new JSONObject();
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        else{
            res.put("statusText", "No autorizado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }


    @GetMapping(CONSULTAR_TODOS_USUARIOS_URL)
    public ResponseEntity<JSONObject> all() {
        List<User> listaUsers = repo.findAll();
        JSONArray array = new JSONArray();
        for (User u : listaUsers)
            array.add(new UserResponse(u));
        JSONObject res = new JSONObject();
        res.put("users", array);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
