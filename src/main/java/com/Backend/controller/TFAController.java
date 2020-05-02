package com.Backend.controller;

import com.Backend.exception.UserNotFoundException;
import com.Backend.model.User;
import com.Backend.model.request.user.UserRegisterRequest;
import com.Backend.repository.IUserRepo;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.Backend.utils.JsonUtils.peticionErronea;
import static com.Backend.utils.TokenUtils.getJWTToken;
import static com.Backend.utils.TokenUtils.getUserFromRequest;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.OPTIONS})
public class TFAController {

    /* URLs que no son accesibles desde ninguna otra clase */
    private static final String LOGOUT_USUARIO_URL = "api/2FA/logout";
    private static final String TOKEN_USUARIO_URL = "/api/2FA/token";
    private static final String LOGIN_2FA_URL =  "/api/2FA/login";
    private static final String GET_2FA_KEY = "/api/2FA/get2FAkey";

    @Autowired
    IUserRepo repo;



    @PostMapping(LOGIN_2FA_URL)
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
        recuperado.setLoggedIn2FA(true);
        repo.save(recuperado);
        String token = getJWTToken(recuperado, recuperado.getMasterPassword());
        res.put("token", token);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping(GET_2FA_KEY)
    public ResponseEntity<JSONObject> get2FAkey(HttpServletRequest request) throws UserNotFoundException {
        JSONObject res = new JSONObject();
        User usuario = getUserFromRequest(request, repo);
        usuario.updateSecret();
        repo.save(usuario);
        res.put("key", usuario.getSecret());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping(LOGOUT_USUARIO_URL)
    public ResponseEntity<JSONObject> logout(HttpServletRequest request) throws UserNotFoundException {
        JSONObject res = new JSONObject();
        User usuario = getUserFromRequest(request, repo);
        usuario.setLoggedIn2FA(false);
        repo.save(usuario);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping(TOKEN_USUARIO_URL)
    public ResponseEntity<JSONObject> token(HttpServletRequest request) throws UserNotFoundException {
        JSONObject res = new JSONObject();
        User usuario = getUserFromRequest(request, repo);
        String token = getJWTToken(usuario, usuario.getMasterPassword());
        res.put("token", token);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }



    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
