package com.Backend.controller;

import com.Backend.exception.CategoryNotFoundException;
import com.Backend.exception.PasswordNotFoundException;
import com.Backend.exception.UserNotFoundException;
import com.Backend.model.OwnsPassword;
import com.Backend.model.Password;
import com.Backend.model.User;
import com.Backend.model.request.DeleteByIdRequest;
import com.Backend.model.request.InsertPasswordRequest;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.Backend.utils.TokenUtils.getUserIdFromRequest;

@RestController
public class PasswordController {

    /* URLs que no son accesibles desde ninguna otra clase */
    public static final String INSERTAR_PASSWORD_URL = "/api/contrasenya/insertar";
    public static final String ELIMINAR_PASSWORD_URL = "/api/contrasenya/eliminar";
    public static final String LISTAR_PASSWORDS_USUARIO_URL = "/api/contrasenya/listar";

    @Autowired
    IPassRepo repoPass;

    @Autowired
    IUserRepo repoUser;

    @Autowired
    IOwnsPassRepo repoOwnsPass;

    @Autowired
    ICatRepo repoCat;

    public User getUserFromRequest(HttpServletRequest request) throws UserNotFoundException{
        Long id = getUserIdFromRequest(request);
        User usuario = repoUser.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return usuario;
    }

    @PostMapping(INSERTAR_PASSWORD_URL)
    public ResponseEntity<JSONObject> insertar(HttpServletRequest request,
                                               @RequestBody InsertPasswordRequest passReq)
            throws UserNotFoundException {

        JSONObject res = new JSONObject();
        if(!passReq.isValid()){
            res.put("statusText", "Los campos no pueden quedar vacíos.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        try{
            User user = getUserFromRequest(request);
            Password password = passReq.getAsPassword();
            password.setCategory(repoCat.findById(passReq.getPasswordCategoryId()).orElseThrow(() -> new CategoryNotFoundException(passReq.getPasswordCategoryId())));

            //Se comprueba que el usuario no tiene una contraseña con el mismo nombre
            List<OwnsPassword> allops = repoOwnsPass.findAllByUser(user);
            for (OwnsPassword i:allops){
                System.out.println((i.getPassword()).getPasswordName());
                if((i.getPassword()).getPasswordName().equals(password.getPasswordName())){
                    res.put("statusText", "Ya existe una contraseña con el mismo nombre para el usuario");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
                }
            }

            repoPass.save(password);
            OwnsPassword ownsp = new OwnsPassword(user, password, 1);
            repoOwnsPass.save(ownsp);
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        catch(UserNotFoundException e){
            res.put("statusText", "Usuario no existente");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        catch (CategoryNotFoundException e) {
            e.printStackTrace();
            res.put("statusText", "Categoría no encontrada.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    @GetMapping(LISTAR_PASSWORDS_USUARIO_URL)
    public ResponseEntity<JSONObject> listar(HttpServletRequest request)
            throws UserNotFoundException {

        JSONObject res = new JSONObject();
        try {
            User user = getUserFromRequest(request);
            List<OwnsPassword> allops = repoOwnsPass.findAllByUser(user);

            JSONArray allpass = new JSONArray();

            for (OwnsPassword i:allops){
                PasswordResponse pres = new PasswordResponse(i);
                JSONObject a = new JSONObject();
                a.put("passId", pres.getPassId());
                a.put("passwordName", pres.getPasswordName());
                a.put("catId", pres.getCatId());
                a.put("categoryName", pres.getCategoryName());
                a.put("rol", pres.getRol());
                a.put("optionalText",pres.getOptionalText());
                a.put("userName", pres.getUserName());
                allpass.add(a);
            }
            res.put("passwords", allpass);
            return ResponseEntity.status(HttpStatus.OK).body(res);

        }
        catch(UserNotFoundException e){
            res.put("statusText", "Usuario no existente");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    @DeleteMapping(ELIMINAR_PASSWORD_URL)
    public ResponseEntity<JSONObject> eliminar(HttpServletRequest request,
                                               @RequestBody DeleteByIdRequest deleteIdReq) throws UserNotFoundException, PasswordNotFoundException {

        JSONObject res = new JSONObject();
        if(!deleteIdReq.isValid()){
            res.put("statusText", "Los campos no pueden quedar vacíos.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        try {
            User user = getUserFromRequest(request);

            Long idPass = deleteIdReq.getId();
            Password password = repoPass.findById(idPass).orElseThrow(() -> new PasswordNotFoundException(idPass));
            OwnsPassword ops = repoOwnsPass.findByPasswordAndUser(password, user);

            if (ops.getRol() == 1) {
                //Eres el usuario creador
                List<OwnsPassword> allops;
                allops = repoOwnsPass.findAllByPassword(password);
                for (OwnsPassword i : allops) {
                    repoOwnsPass.delete(i);
                }
                repoPass.delete(password);
            } else {
                //No eres el usuario creador
                repoOwnsPass.delete(ops);
            }
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        catch(UserNotFoundException e){
            res.put("statusText", "Usuario no existente");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        catch(PasswordNotFoundException e){
            res.put("statusText", "Contraseña no existente");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

}
