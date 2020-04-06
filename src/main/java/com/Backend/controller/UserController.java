package com.Backend.controller;

import com.Backend.exception.UserNotFoundException;
import com.Backend.model.Category;
import com.Backend.model.OwnsPassword;
import com.Backend.model.User;
import com.Backend.model.request.ModifyUserRequest;
import com.Backend.model.request.UserRegisterRequest;
import com.Backend.model.response.UserResponse;
import com.Backend.repository.ICatRepo;
import com.Backend.repository.IOwnsPassRepo;
import com.Backend.repository.IPassRepo;
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
import static com.Backend.utils.JsonUtils.peticionCorrecta;
import static com.Backend.utils.JsonUtils.peticionErronea;
import static com.Backend.utils.PasswordUtils.modifyPasswordsAtCategoryDelete;
import static com.Backend.utils.TokenUtils.*;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.OPTIONS})
public class UserController {

    /* URLs que no son accesibles desde ninguna otra clase */
    private static final String LOGOUT_USUARIO_URL = "api/usuarios/logout";
    private static final String TOKEN_USUARIO_URL = "/api/usuarios/token";
    private static final String CONSULTAR_USUARIO_URL =  "/api/usuarios/consultar";
    private static final String ELIMINAR_USUARIO_URL = "/api/usuarios/eliminar";
    private static final String MODIFICAR_USUARIO_URL = "/api/usuarios/modificar";

    @Autowired
    IUserRepo repo;
    @Autowired
    ICatRepo repoCat;
    @Autowired
    IPassRepo repoPass;
    @Autowired
    IOwnsPassRepo repoOwns;

    @PostMapping(REGISTRO_USUARIO_URL)
    public ResponseEntity<JSONObject> registro(@RequestBody UserRegisterRequest userRegReq) {

        if (!userRegReq.isValid()) {
            return peticionErronea("Los campos no pueden quedar vacíos.");
        }
        if (repo.existsByMail(userRegReq.getMail())) {
            return peticionErronea("El email ya está asociado a una cuenta.");
        }
        userRegReq.setMasterPassword(new BCryptPasswordEncoder().encode(userRegReq.getMasterPassword()));
        repo.save(userRegReq.getAsUser());
        // Si no buscas un usuario con id falla la inserción.
        User usuario = repo.findByMail(userRegReq.getMail());
        repoCat.save(new Category("Sin categoría", usuario));
        return peticionCorrecta();
    }

    @PostMapping(LOGIN_USUARIO_URL)
    public ResponseEntity<JSONObject> login(@RequestBody UserRegisterRequest userRegReq) {
        JSONObject res = new JSONObject();
        if (!userRegReq.isValid()){
            return peticionErronea("Los campos no pueden quedar vacíos");
        }
        if(!repo.existsByMail(userRegReq.getMail())) {
            return peticionErronea("Usuario inexistente.");
        }
        User recuperado = repo.findByMail(userRegReq.getMail());
        BCryptPasswordEncoder b = new BCryptPasswordEncoder();

        if (!b.matches(userRegReq.getMasterPassword(), recuperado.getMasterPassword())) {
            return peticionErronea("Credenciales incorrectos.");
        }
        String token = getJWTToken(recuperado, recuperado.getMasterPassword());
        res.put("token", token);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping(TOKEN_USUARIO_URL)
    public ResponseEntity<JSONObject> token(HttpServletRequest request) throws UserNotFoundException {
        Long id = getUserIdFromRequest(request);
        JSONObject res = new JSONObject();
        if (!repo.existsById(id)) {
            return peticionErronea("El usuario inexistente.");
        }
        User usuario = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        String token = getJWTToken(usuario, usuario.getMasterPassword());
        res.put("token", token);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping(CONSULTAR_USUARIO_URL)
    public ResponseEntity<JSONObject> consulta(HttpServletRequest request) throws UserNotFoundException {
        Long id = getUserIdFromRequest(request);
        JSONObject res = new JSONObject();
        if (!repo.existsById(id)){
            return peticionErronea("Usuario inexistente.");
        }
        User usuario = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        res.put("user", new UserResponse(usuario));
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @DeleteMapping(ELIMINAR_USUARIO_URL)
    public ResponseEntity<JSONObject> eliminar(HttpServletRequest request) throws UserNotFoundException {
        User user = getUserFromRequest(request, repo);
        if (!repo.existsById(user.getId())) {
            return peticionErronea("Usuario inexistente.");
        }

        List<OwnsPassword> ownpass = repoOwns.findAllByUser(user);
        for (OwnsPassword owp : ownpass){
            if(owp.getRol() == 1){
                //Cascada hacia todos los repoOwn
                repoPass.delete(owp.getPassword());
            }else{
                repoOwns.delete(owp);
            }
        }

        //cascada hacia categorías, que ya no tienen contraseñas
        repo.deleteById(user.getId());
        return peticionCorrecta();
    }

    @PostMapping(MODIFICAR_USUARIO_URL)
    public ResponseEntity<JSONObject> modify(HttpServletRequest request,
                                             @RequestBody ModifyUserRequest userModReq) throws UserNotFoundException {

        if (!userModReq.isValid()) {
            return peticionErronea("Los campos no pueden quedar vacíos.");
        }
        User userId = getUserFromRequest(request, repo);
        if (!repo.existsById(userId.getId())) {
            return peticionErronea("Usuario inexistente.");
        }
        User fetchedUser = repo.findById(userId.getId()).orElseThrow(() -> new UserNotFoundException(userId.getId()));
        BCryptPasswordEncoder b = new BCryptPasswordEncoder();
        if (b.matches(userModReq.getOldMasterPassword(), fetchedUser.getMasterPassword())
                && fetchedUser.getMail().equals(userModReq.getMail())) {

            String newHashedPassword = b.encode(userModReq.getNewMasterPassword());
            fetchedUser.setMasterPassword(newHashedPassword);
            repo.save(fetchedUser);
            JSONObject res = new JSONObject();
            res.put("token", getJWTToken(fetchedUser, newHashedPassword));
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } else {
            return peticionErronea("Credenciales incorrectos.");
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
