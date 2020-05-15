package com.Backend.controller;

import com.Backend.exception.UserNotFoundException;
import com.Backend.model.Category;
import com.Backend.model.OwnsPassword;
import com.Backend.model.Password;
import com.Backend.model.User;
import com.Backend.model.request.UserLoginRequest;
import com.Backend.model.request.user.ModifyUserRequest;
import com.Backend.model.request.user.UserRegisterRequest;
import com.Backend.model.response.UserResponse;
import com.Backend.repository.ICatRepo;
import com.Backend.repository.IOwnsPassRepo;
import com.Backend.repository.IPassRepo;
import com.Backend.repository.IUserRepo;
import com.Backend.utils.SendGridEmailService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.Backend.security.SecurityConstants.*;
import static com.Backend.utils.JsonUtils.peticionCorrecta;
import static com.Backend.utils.JsonUtils.peticionErronea;
import static com.Backend.utils.TokenUtils.*;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.OPTIONS})
public class UserController {

    /* URLs que no son accesibles desde ninguna otra clase */
    private static final String TOKEN_USUARIO_URL = "/api/usuarios/token";
    private static final String CONSULTAR_USUARIO_URL =  "/api/usuarios/consultar";
    private static final String ELIMINAR_USUARIO_URL = "/api/usuarios/eliminar";
    private static final String MODIFICAR_USUARIO_URL = "/api/usuarios/modificar";
    private static final String LOGIN_USUARIO2FA_URL = "/api/usuarios/loginCon2FA";
    private static final String VERIFICAR_USUARIO_URL = "/api/usuarios/verificar";
    @Autowired
    IUserRepo repo;
    @Autowired
    ICatRepo repoCat;
    @Autowired
    IPassRepo repoPass;
    @Autowired
    IOwnsPassRepo repoOwns;

    @Autowired
    SendGridEmailService senGridService;


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
        senGridService.sendHTML("pandora.app.unizar@gmail.com", userRegReq.getMail(), "Confirme su cuenta", getVerificationUrl(usuario.getId()));
        repoCat.save(new Category("Compartida", usuario));
        repoCat.save(new Category("Sin categoría", usuario));
        repoCat.save(new Category("Redes sociales", usuario));
        repoCat.save(new Category("Cuentas bancarias", usuario));
        repoCat.save(new Category("Tarjetas de crédito", usuario));
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

    @PostMapping(LOGIN_USUARIO2FA_URL)
    public ResponseEntity<JSONObject> loginCon2FA(@RequestBody UserLoginRequest userLogReq) {
        JSONObject res = new JSONObject();
        if (!userLogReq.isValid()){
            return peticionErronea("Los campos no pueden quedar vacíos");
        }
        if(!repo.existsByMail(userLogReq.getMail())) {
            return peticionErronea("Usuario inexistente.");
        }
        User recuperado = repo.findByMail(userLogReq.getMail());
        BCryptPasswordEncoder b = new BCryptPasswordEncoder();

        if(!recuperado.getMailVerified()){
            return peticionErronea("Correo no verificado.");
        }

        if (!b.matches(userLogReq.getMasterPassword(), recuperado.getMasterPassword())) {
            return peticionErronea("Credenciales incorrectos.");
        }

        Totp totp = new Totp(recuperado.getSecret());
        if (!userLogReq.getVerificationCode().equals(recuperado.getSecret())) {
            return peticionErronea("Codigo 2FA incorrecto.");
        }
        if (recuperado.getSecretExpirationTime() < System.currentTimeMillis() ){
            return peticionErronea("Codigo 2FA expirado.");
        }

        String token = getJWTToken(recuperado, recuperado.getMasterPassword());
        res.put("token", token);
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

    @GetMapping(CONSULTAR_USUARIO_URL)
    public ResponseEntity<JSONObject> consulta(HttpServletRequest request) throws UserNotFoundException {
        String id = getUserIdFromRequest(request);
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
        User fetchedUser = getUserFromRequest(request, repo);
        BCryptPasswordEncoder b = new BCryptPasswordEncoder();
        if (b.matches(userModReq.getOldMasterPassword(), fetchedUser.getMasterPassword())
                && fetchedUser.getMail().equals(userModReq.getMail())) {

            List<OwnsPassword> ownpasswords = repoOwns.findAllByUserAndRol(fetchedUser, 1);
            changeEncode(ownpasswords, userModReq.getOldMasterPassword(), userModReq.getNewMasterPassword());


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

    @GetMapping(VERIFICAR_USUARIO_URL)
    public String verify(@RequestParam String id) throws UserNotFoundException {

        User user = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        user.setMailVerified(true);
        repo.save(user);
        JSONObject res = new JSONObject();
        res.put("OK", "OK");

        return "<h1>Pandora</h1><p>&nbsp;</p><p>Mail confirmado, ya puede iniciar sesi&oacute;n&nbsp;<a title=\"Pandora\" href=\"http://app-pandora.herokuapp.com/home\">aqu&iacute;</a>&nbsp;.</p><p>&nbsp;</p>";
    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private String getVerificationUrl(String id){
        return"<h1>Pandora</h1><p>&nbsp;</p><p>Por favor, confirme su cuenta entrando en el siguiente&nbsp;<a title=\"enlace\" href=\"https://pandorapp.herokuapp.com/api/usuarios/verificar?id=" + id + "\">enlace</a></p>";
    }

    private void changeEncode(List<OwnsPassword> ownpasswords, String oldPass, String newPass){
        TextEncryptor oldTextEncryptor = Encryptors.text(oldPass, "46b930");
        TextEncryptor newTextEncryptor = Encryptors.text(newPass, "46b930");
        for(OwnsPassword opass : ownpasswords){
            Password pass = opass.getPassword();
            pass.setPassword(newTextEncryptor.encrypt(oldTextEncryptor.decrypt(pass.getPassword())));
            repoPass.save(pass);
        }
    }
}
