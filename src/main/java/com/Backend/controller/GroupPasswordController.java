package com.Backend.controller;

import com.Backend.exception.CategoryNotFoundException;
import com.Backend.exception.PasswordNotFoundException;
import com.Backend.exception.UserNotFoundException;
import com.Backend.model.*;
import com.Backend.model.request.groupPassword.InsertGroupPasswordRequest;
import com.Backend.model.request.groupPassword.ModifyGroupPasswordRequest;
import com.Backend.model.response.PasswordResponse;
import com.Backend.repository.ICatRepo;
import com.Backend.repository.IOwnsPassRepo;
import com.Backend.repository.IPassRepo;
import com.Backend.repository.IUserRepo;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static com.Backend.security.SecurityConstants.SUPER_SECRET_KEY;
import static com.Backend.utils.JsonUtils.peticionErronea;
import static com.Backend.utils.PasswordCheckUtils.generarJSONPassword;
import static com.Backend.utils.TokenUtils.getUserFromRequest;

@RestController
public class GroupPasswordController {
    /* URLs que no son utilizadas desde ninguna otra clase */
    public static final String INSERTAR_GROUP_PASSWORD_URL = "/api/grupo/insertar";
    public static final String MODIFICAR_GROUP_PASSWORD_URL = "/api/grupo/modificar";
    public static final String LISTAR_GROUP_PASSWORDS_URL = "/api/grupo/listar";
    public static final String ELIMINAR_GROUP_PASSWORD_URL = "/api/grupo/eliminar";

    @Autowired
    IPassRepo repoPass;
    @Autowired
    IUserRepo repoUser;
    @Autowired
    IOwnsPassRepo repoOwnsPass;
    @Autowired
    ICatRepo repoCat;

    @PostMapping(INSERTAR_GROUP_PASSWORD_URL)
    public ResponseEntity<JSONObject> insertarGroupPassword(HttpServletRequest request,
                                        @RequestBody InsertGroupPasswordRequest passReq) {
        if (!passReq.isValid()) {
            return peticionErronea("Los campos no pueden quedar vacíos.");
        }
        try {
            User user = getUserFromRequest(request, repoUser);
            Password password = passReq.getAsPassword();
            password.setCategory(repoCat.findById(passReq.getPasswordCategoryId()).orElseThrow(()
                    -> new CategoryNotFoundException(passReq.getPasswordCategoryId())));

            if(checkSameNamePassword(user, password))
                return peticionErronea("Ya existe una contraseña con el mismo nombre para el usuario");
            else {
                LinkedList<String> mails = new LinkedList<>();
                anyadeGroupPasswordAUsuarios(password, user, passReq.getUsuarios(), mails, true);
                JSONObject wrong = new JSONObject();
                wrong.put("usuariosErroneos", mails);
                return ResponseEntity.status(HttpStatus.OK).body(wrong);
            }
        } catch (UserNotFoundException e) {
            return peticionErronea("Usuario no existente.");
        } catch (CategoryNotFoundException e) {
            return peticionErronea("Categoría no encontrada.");
        }
    }

    @PostMapping(MODIFICAR_GROUP_PASSWORD_URL)
    public ResponseEntity<JSONObject> modificarGroupPassword(HttpServletRequest request,
                                                            @RequestBody ModifyGroupPasswordRequest passReq) {
        if (!passReq.isValid()) {
            return peticionErronea("Los campos no pueden quedar vacíos.");
        }

        try {
            User user = getUserFromRequest(request, repoUser);
            Password password = repoPass.findById(passReq.getPassId()).orElseThrow(()
                    -> new PasswordNotFoundException(passReq.getPassId()));

            if(soyPropietario(user, password)) {
                if (checkSameNamePassword(user, password)) {
                    return peticionErronea("Ya existe una contraseña con el mismo nombre para el usuario");
                } else {
                    password.setPasswordName(passReq.getPasswordName());
                    if(!passReq.getPasswordCategoryId().equals(password.getCategory().getId())){
                        Category cat = repoCat.findById(passReq.getPasswordCategoryId()).orElseThrow(
                                () -> new CategoryNotFoundException(passReq.getPasswordCategoryId()));
                        if(esPropietarioDeCat(user, cat)){
                            password.setCategory(cat);
                        } else {
                            return peticionErronea("No eres propietario de la categoría");
                        }
                    }
                    password.setOptionalText(passReq.getOptionalText());
                    password.setUserName(passReq.getUserName());

                    LocalDate ld = LocalDate.now();
                    ld = ld.plusDays(passReq.getExpirationTime());
                    password.setExpirationTime(ld);

                    LinkedList<String> mails = passReq.getUsuarios();
                    LinkedList<String> noEncontrados = new LinkedList<>();
                    password.setPassword(passReq.getPassword());
                    repoOwnsPass.deleteByPasswordKeyAndRolNot(password.getId(), 1);
                    anyadeGroupPasswordAUsuarios(password, user, mails, noEncontrados, false);
                    JSONObject wrong = new JSONObject();
                    wrong.put("usuariosErroneos", noEncontrados);
                    return ResponseEntity.status(HttpStatus.OK).body(wrong);
                }
            } else {
                return peticionErronea("No eres el propietario.");
            }
        } catch (UserNotFoundException e) {
            return peticionErronea("Usuario no existente.");
        } catch (CategoryNotFoundException e) {
            return peticionErronea("Categoría no encontrada.");
        } catch (PasswordNotFoundException e) {
            return peticionErronea("La contraseña no existe.");
        }
    }

    @GetMapping(LISTAR_GROUP_PASSWORDS_URL)
    public ResponseEntity<JSONObject> listar(HttpServletRequest request){
        try {
            User user = getUserFromRequest(request, repoUser);
            List<OwnsPassword> allops = repoOwnsPass.findAllByUser(user);
            TextEncryptor textEncryptor = Encryptors.text(SUPER_SECRET_KEY, "46b930");
            return getGroupPasswords(allops, textEncryptor, user);
        } catch (UserNotFoundException e) {
            return peticionErronea("Usuario no existente.");
        }
    }

    /*@DeleteMapping(ELIMINAR_GROUP_PASSWORD_URL)
    public ResponseEntity<JSONObject> eliminar_group_password(HttpServletRequest request,
                                               @RequestParam Long id){

        PasswordController groupPassword = new PasswordController();
        return groupPassword.eliminar(request, id);
    }*/

    // Devuelve una respuesta a peticion http con todas las passwords que hayan sido compartidas.
    public ResponseEntity<JSONObject> getGroupPasswords(List<OwnsPassword> ownspass, TextEncryptor enc, User user){
        JSONArray allpass = new JSONArray();
        JSONObject res = new JSONObject();
        for(OwnsPassword op : ownspass){
            if(op.getGrupo()==1){ // Otro usuario distinto la tiene
                PasswordResponse pres = new PasswordResponse(op);
                JSONObject a = generarJSONPassword(pres, enc);
                if(op.getRol()==0){
                    a.remove("catId");
                    a.remove("categoryName");
                    a.put("catId", -1);
                    a.put("categoryName", "Compartida");
                }
                allpass.add(a);
            }
        }
        res.put("passwords", allpass);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    //Se comprueba que el usuario no tiene una contraseña con el mismo nombre
    public boolean checkSameNamePassword(User user, Password password){
        boolean existe = false;
        List<OwnsPassword> allops = repoOwnsPass.findAllByUser(user);
        for (OwnsPassword i : allops) {
            if ((i.getPassword()).getPasswordName().equals(password.getPasswordName())
                    && !i.getPassword().getId().equals(password.getId())) {
                existe = true;
                break;
            }
        }
        return existe;
    }

    // Añade la contraseña al usuario propietario y a todos los demás usuarios (si existan)
    // Si el usuario se añade a sí mismo en el grupo, no se le añade la password
    public void anyadeGroupPasswordAUsuarios(Password password, User propietario, LinkedList<String> mails,
                                             LinkedList<String> noEncontrados, boolean addOwner){

        // Contraseña cifrada con SSK
        TextEncryptor textEncryptor = Encryptors.text(SUPER_SECRET_KEY, "46b930");
        password.setPassword(textEncryptor.encrypt(password.getPassword()));
        repoPass.save(password);
        if(addOwner)
            repoOwnsPass.save(new OwnsPassword(propietario, password, 1, 1));
        anyadeANoPropietarios(password, propietario, mails, noEncontrados);
    }

    // Añade, si es posible, la password a todos los usuarios de mails como no propietarios. Si no
    // existen, los añade a noEncontrados.
    public void anyadeANoPropietarios(Password password, User propietario, LinkedList<String> mails,
                                      LinkedList<String> noEncontrados){
        for(String mail : mails){
            try {
                if(!mail.equals(propietario.getMail())) {
                    anyadePasswordANoPropietario(password, mail);
                }
            } catch(UserNotFoundException e) {
                noEncontrados.add(e.getMail());
            }
        }
    }

    // Añade al usuario la contraseña como no propietario, si el usuario ya es el propietario y
    // se ha añadido a si mismo, no hace nada.
    public void anyadePasswordANoPropietario(Password password, String mail)
            throws UserNotFoundException{
        if(repoUser.existsByMail(mail)) {
            User usuario = repoUser.findByMail(mail);
            repoOwnsPass.save(new OwnsPassword(usuario, password, 0, 1));
        }
        else{
            throw new UserNotFoundException(mail);
        }
    }

    // Devuelve cierto si y solo si usuario user es el propietario de la contraseña password
    boolean soyPropietario(User user, Password password){
        if(repoOwnsPass.existsByPasswordAndUserAndGrupo(password, user, 1)){
            OwnsPassword ops = repoOwnsPass.findByPasswordAndUser(password, user);
            return (ops.getRol() == 1);
        } else {
            return false;
        }
    }

    // Devuelve true si y solo si la categoría cat pertenece al usuario user
    boolean esPropietarioDeCat(User user, Category cat){
        return cat.getUsuario().getId().equals(user.getId());
    }
}
