package com.Backend.controller;

import com.Backend.exception.CategoryNotFoundException;
import com.Backend.exception.PasswordNotFoundException;
import com.Backend.exception.UserNotFoundException;
import com.Backend.model.Category;
import com.Backend.model.OwnsPassword;
import com.Backend.model.Password;
import com.Backend.model.User;
import com.Backend.model.request.*;
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

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import static com.Backend.utils.JsonUtils.peticionCorrecta;
import static com.Backend.utils.JsonUtils.peticionErronea;
import static com.Backend.utils.PasswordUtils.generateStrongPassword;
import static com.Backend.utils.TokenUtils.getUserFromRequest;

@RestController
public class PasswordController {

    /* URLs que no son utilizadas desde ninguna otra clase */
    public static final String INSERTAR_PASSWORD_URL = "/api/contrasenya/insertar";
    public static final String ELIMINAR_PASSWORD_URL = "/api/contrasenya/eliminar";
    public static final String LISTAR_PASSWORDS_USUARIO_URL = "/api/contrasenya/listar";
    public static final String MODIFICAR_PASSWORDS_USUARIO_URL = "/api/contrasenya/modificar";
    public static final String GENERAR_PASSWORD_URL = "/api/contrasenya/generar";

    @Autowired
    IPassRepo repoPass;
    @Autowired
    IUserRepo repoUser;
    @Autowired
    IOwnsPassRepo repoOwnsPass;
    @Autowired
    ICatRepo repoCat;

    @PostMapping(INSERTAR_PASSWORD_URL)
    public ResponseEntity<JSONObject> insertar(HttpServletRequest request,
                                               @RequestBody InsertPasswordRequest passReq) {

        JSONObject res = new JSONObject();
        if (!passReq.isValid()) {
            return peticionErronea("Los campos no pueden quedar vacíos.");
        }
        try {
            User user = getUserFromRequest(request, repoUser);
            Password password = passReq.getAsPassword();
            password.setCategory(repoCat.findById(passReq.getPasswordCategoryId()).orElseThrow(() -> new CategoryNotFoundException(passReq.getPasswordCategoryId())));

            //Se comprueba que el usuario no tiene una contraseña con el mismo nombre
            List<OwnsPassword> allops = repoOwnsPass.findAllByUser(user);
            for (OwnsPassword i : allops) {
                if ((i.getPassword()).getPasswordName().equals(password.getPasswordName())) {
                    return peticionErronea("Ya existe una contraseña con el mismo nombre para el usuario");
                }
            }
            repoPass.save(password);
            OwnsPassword ownsp = new OwnsPassword(user, password, 1);
            repoOwnsPass.save(ownsp);
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } catch (UserNotFoundException e) {
            return peticionErronea("Usuario no existente.");
        } catch (CategoryNotFoundException e) {
            return peticionErronea("Categoría no encontrada.");
        }
    }

    @PostMapping(LISTAR_PASSWORDS_USUARIO_URL)
    public ResponseEntity<JSONObject> listar(HttpServletRequest request,
                                             @RequestBody ListPasswordRequest passReq){
        if (!passReq.isValid()) {
            return peticionErronea("Los campos no pueden quedar vacíos.");
        }
        JSONObject res = new JSONObject();
        try {
            User user = getUserFromRequest(request, repoUser);
            List<OwnsPassword> allops = repoOwnsPass.findAllByUser(user);

            JSONArray allpass = new JSONArray();
            TextEncryptor textEncryptor = Encryptors.text(passReq.getMasterPassword(), "46b930");
            for (OwnsPassword i : allops) {
                // En el constructor se calcula los días de diferencia.
                PasswordResponse pres = new PasswordResponse(i);
                JSONObject a = new JSONObject();
                a.put("passId", pres.getPassId());
                a.put("passwordName", pres.getPasswordName());
                a.put("catId", pres.getCatId());
                a.put("categoryName", pres.getCategoryName());
                a.put("rol", pres.getRol());
                a.put("optionalText", pres.getOptionalText());
                a.put("userName", pres.getUserName());
                a.put("password", textEncryptor.decrypt(pres.getPassword()));
                a.put("noDaysBeforeExpiration", pres.getExpirationDate());
                allpass.add(a);
            }
            res.put("passwords", allpass);
            return ResponseEntity.status(HttpStatus.OK).body(res);

        } catch (UserNotFoundException e) {
            return peticionErronea("Usuario no existente.");
        }
    }

    @DeleteMapping(ELIMINAR_PASSWORD_URL)
    public ResponseEntity<JSONObject> eliminar(HttpServletRequest request,
                                               @RequestParam Long id) {

        JSONObject res = new JSONObject();
        if (id == null) {
            res.put("statusText", "Los campos no pueden quedar vacíos.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        try {
            User user = getUserFromRequest(request, repoUser);

            Password password = repoPass.findById(id).orElseThrow(() -> new PasswordNotFoundException(id));
            OwnsPassword ops = repoOwnsPass.findByPasswordAndUser(password, user);

            if (ops.getRol() == 1) {
                repoPass.delete(password);
            } else {
                //No eres el usuario creador
                repoOwnsPass.delete(ops);
            }
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } catch (UserNotFoundException e) {
            res.put("statusText", "Usuario no existente");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        } catch (PasswordNotFoundException e) {
            res.put("statusText", "Contraseña no existente");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    @PostMapping(MODIFICAR_PASSWORDS_USUARIO_URL)
    public ResponseEntity<JSONObject> modificar(@RequestBody ModifyPasswordRequest passReq, HttpServletRequest request) {

        if (!passReq.isValid()) {
            return peticionErronea("Los campos no pueden quedar vacíos.");
        }
        try {
            User user = getUserFromRequest(request, repoUser);

            Long idPass = passReq.getId();
            Password password = repoPass.findById(idPass).orElseThrow(() -> new PasswordNotFoundException(idPass));
            OwnsPassword ops = repoOwnsPass.findByPasswordAndUser(password, user);
            if (!(ops.getRol() == 1)) {
                return peticionErronea("No eres el propietario");
            }
            //Eres el usuario creador
            if (passReq.getPasswordName() != null) {
                List<OwnsPassword> allops = repoOwnsPass.findAllByUser(user);
                for (OwnsPassword i : allops) {
                    if(i.getPassword().getId() != idPass){
                        if ((i.getPassword()).getPasswordName().equals(passReq.getPasswordName())) {
                            return peticionErronea("Ya existe una contraseña con el mismo nombre para el usuario");
                        }
                    }
                }
                password.setPasswordName(passReq.getPasswordName());
            }
            if (passReq.getPasswordCategoryId() != null) {
                Category newCat = repoCat.findById(passReq.getPasswordCategoryId()).orElseThrow(() -> new CategoryNotFoundException(passReq.getPasswordCategoryId()));
                password.setCategory(newCat);
            }
            if (passReq.getPassword() != null) {
                TextEncryptor textEncryptor = Encryptors.text(passReq.getMasterPassword(), "46b930");
                password.setPassword(textEncryptor.encrypt(passReq.getPassword()));
            }
            if (passReq.getOptionalText() != null) password.setOptionalText(passReq.getOptionalText());
            if (passReq.getUserName() != null) password.setUserName(passReq.getUserName());
            if (passReq.getExpirationTime() != null){
                LocalDate ld = LocalDate.now();
                ld = ld.plusDays(passReq.getExpirationTime());
                password.setExpirationTime(ld);
            }
            repoPass.save(password);
            return peticionCorrecta();
        } catch (UserNotFoundException e) {
            return peticionErronea("Usuario no existente");
        } catch (PasswordNotFoundException e) {
            return peticionErronea("Contraseña no existente");
        } catch (CategoryNotFoundException e) {
            return peticionErronea("Categoría no encontrada.");
        }
    }

    @PostMapping(GENERAR_PASSWORD_URL)
    public ResponseEntity<JSONObject> generar(@RequestBody GeneratePasswordRequest req) {
        if(req.isValid()){
            String pass = generateStrongPassword(req.getMinus(), req.getMayus(),
                    req.getNumbers(), req.getSpecialCharacters(), req.getLength());
            JSONObject devolver = new JSONObject();
            devolver.put("password", pass);
            return ResponseEntity.status(HttpStatus.OK).body(devolver);
        }
        else{
            return peticionErronea("Parámetros incorrectos.");
        }

    }
}
