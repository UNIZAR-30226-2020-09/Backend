package com.Backend.controller;

import com.Backend.exception.UserNotFoundException;
import com.Backend.model.OwnsPassword;
import com.Backend.model.Password;
import com.Backend.model.User;
import com.Backend.model.request.user.UserRegisterRequest;
import com.Backend.model.request.user.VerifyResetRequest;
import com.Backend.repository.IOwnsPassRepo;
import com.Backend.repository.IPassRepo;
import com.Backend.repository.IUserRepo;
import com.Backend.utils.SendGridEmailService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.Backend.utils.JsonUtils.peticionErronea;
import static com.Backend.utils.TokenUtils.getJWTToken;
import static com.Backend.utils.TokenUtils.getUserFromRequest;


@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.OPTIONS})
public class TFAController {

    /* URLs */
    private static final String LOGOUT_USUARIO_URL = "api/2FA/logout";
    private static final String LOGIN_2FA_URL =  "/api/2FA/login";
    private static final String GET_2FA_KEY = "/api/2FA/get2FAkey";
    private static final String RECUPERAR_USUARIO_URL = "/api/2FA/recuperar";
    private static final String VERIFICAR_RESET_USUARIO_URL = "/api/2FA/verificarReset";

    @Autowired
    IUserRepo repo;
    @Autowired
    IPassRepo repoPass;
    @Autowired
    IOwnsPassRepo repoOwns;

    @Autowired
    SendGridEmailService senGridService;



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
        if(!recuperado.getMailVerified()){
            return peticionErronea("Correo no verificado.");
        }
        if (recuperado.getLoggedIn2FA() == true){
            return peticionErronea("Ya se ha iniciado sesión en otro dispositivo.");
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

    @PostMapping(RECUPERAR_USUARIO_URL)
    public ResponseEntity<JSONObject> recuperar(@RequestBody UserRegisterRequest recuRequest) throws UserNotFoundException {
        JSONObject res = new JSONObject();
        if (!recuRequest.isValid()){
            return peticionErronea("Los campos no pueden quedar vacíos");
        }
        if(!repo.existsByMail(recuRequest.getMail())) {
            return peticionErronea("Usuario inexistente.");
        }
        User usuario = repo.findByMail(recuRequest.getMail());
        BCryptPasswordEncoder b = new BCryptPasswordEncoder();
        if (!b.matches(recuRequest.getMasterPassword(), usuario.getMasterPassword())) {
            return peticionErronea("Credenciales incorrectos.");
        }

        if(!usuario.getMailVerified()){
            return peticionErronea("Correo no verificado.");
        }

        usuario.generateResetCode();
        repo.save(usuario);
        senGridService.sendHTML("pandora.app.unizar@gmail.com", recuRequest.getMail(), "Código restauración 2FA", getResetCodeUrl(usuario.getResetCode()));

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping(VERIFICAR_RESET_USUARIO_URL)
    public ResponseEntity<JSONObject> verificarReset(@RequestBody VerifyResetRequest verifyRequest) throws UserNotFoundException {
        JSONObject res = new JSONObject();
        if (!verifyRequest.isValid()){
            return peticionErronea("Los campos no pueden quedar vacíos");
        }
        if(!repo.existsByMail(verifyRequest.getMail())) {
            return peticionErronea("Usuario inexistente.");
        }
        User usuario = repo.findByMail(verifyRequest.getMail());

        BCryptPasswordEncoder b = new BCryptPasswordEncoder();
        if (!b.matches(verifyRequest.getOldMasterPassword(), usuario.getMasterPassword())) {
            return peticionErronea("Credenciales incorrectos.");
        }
        if(verifyRequest.getOldMasterPassword().equals(verifyRequest.getNewMasterPassword())){
            return peticionErronea("La nueva contraseña no puede ser igual.");
        }

        if (!verifyRequest.getResetCode().equals(usuario.getResetCode())) {
            return peticionErronea("Codigo de recuperación incorrecto.");
        }

        usuario.generateResetCode();

        List<OwnsPassword> ownpasswords = repoOwns.findAllByUserAndRol(usuario, 1);
        changeEncode(ownpasswords, verifyRequest.getOldMasterPassword(), verifyRequest.getNewMasterPassword());

        String newHashedPassword = b.encode(verifyRequest.getNewMasterPassword());
        usuario.setMasterPassword(newHashedPassword);

        usuario.setLoggedIn2FA(true);

        repo.save(usuario);

        res.put("token", getJWTToken(usuario, newHashedPassword));
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

    private  String getResetCodeUrl(String code){
        return"<h1>Pandora</h1><p>&nbsp;</p><p>Su código de verificación es: " + code + " por favor, ingréselo en la app PandoraAuth</p>";
    }

    private void changeEncode(List<OwnsPassword> ownpasswords, String oldPass, String newPass){
        TextEncryptor oldTextEncryptor = Encryptors.text(oldPass, "46b930");
        TextEncryptor newTextEncryptor = Encryptors.text(newPass, "46b930");
        for(OwnsPassword opass : ownpasswords){
            Password pass = opass.getPassword();
            if (pass.getCategory().getCategoryName().equals("Compartida")) continue;
            pass.setPassword(newTextEncryptor.encrypt(oldTextEncryptor.decrypt(pass.getPassword())));
            repoPass.save(pass);
        }
    }

}
