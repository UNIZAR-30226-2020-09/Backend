package com.Backend.controller;

import com.Backend.exception.PasswordNotFoundException;
import com.Backend.exception.UserNotFoundException;
import com.Backend.model.OwnsPassword;
import com.Backend.model.Password;
import com.Backend.model.User;
import com.Backend.model.request.DeleteByIdRequest;
import com.Backend.model.request.InsertPasswordRequest;
import com.Backend.repository.IOwnsPassRepo;
import com.Backend.repository.IPassRepo;
import com.Backend.repository.IUserRepo;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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


    @PostMapping(INSERTAR_PASSWORD_URL)
    public ResponseEntity<JSONObject> insertar(HttpServletRequest request,
                                               @RequestBody InsertPasswordRequest passReq)
            throws UserNotFoundException {

        Long userID = getUserIdFromRequest(request);
        JSONObject res = new JSONObject();
        if (passReq.isValid()) {
            Password password = passReq.getAsPassword();
            User user = repoUser.findById(userID).orElseThrow(() -> new UserNotFoundException(userID));

            List<OwnsPassword> allops = repoOwnsPass.findAllByUser(user);
            for (OwnsPassword i:allops){
                System.out.println((i.getPassword()).getPasswordName());
                if((i.getPassword()).getPasswordName().equals(password.getPasswordName())){
                    res.put("statusText", "Ya existe una contraseña con el mismo nombre para el usuario");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
                }
            }
            repoPass.save(password);
            OwnsPassword ownsp = new OwnsPassword(user, password, 1);
            repoOwnsPass.save(ownsp);
            res.put("statusText", "Contraseña insertada");
            return ResponseEntity.status(HttpStatus.OK).body(res);

        } else{
            res.put("statusText", "Faltan campos.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    /*
     * Método para eliminar usuario
     */
    @DeleteMapping(ELIMINAR_PASSWORD_URL)
    public ResponseEntity<JSONObject> eliminar(HttpServletRequest request,
                                               @RequestBody DeleteByIdRequest deleteIdReq) throws UserNotFoundException, PasswordNotFoundException {
        Long idUser = getUserIdFromRequest(request);
        User user = repoUser.findById(idUser).orElseThrow(() -> new UserNotFoundException(idUser));
        JSONObject res = new JSONObject();
        if (deleteIdReq.isValid()) {
            Long idPass = deleteIdReq.getId();
            Password password = repoPass.findById(idPass).orElseThrow(() -> new PasswordNotFoundException(idUser));
            OwnsPassword ops = repoOwnsPass.findByPasswordAndUser(password,user);

            if (ops.getRol() == 1){
                //Eres el usuario creador
                List<OwnsPassword> allops;
                allops = repoOwnsPass.findAllByPassword(password);
                for (OwnsPassword i:allops){
                    repoOwnsPass.delete(i);
                }
                repoPass.delete(password);
            } else{
                //No eres el usuario creador
                repoOwnsPass.delete(ops);
            }

            res.put("statusText", "Contraseña eliminada");
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } else{
            res.put("statusText", "No autorizado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }
    }

}
