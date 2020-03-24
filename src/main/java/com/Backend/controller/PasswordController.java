package com.Backend.controller;

import com.Backend.exception.UserNotFoundException;
import com.Backend.model.Category;
import com.Backend.model.User;
import com.Backend.model.request.InsertDeleteCategoryRequest;
import com.Backend.model.request.InsertPasswordRequest;
import com.Backend.repository.ICatRepo;
import com.Backend.repository.IPassRepo;
import com.Backend.repository.IUserRepo;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PasswordController {

    /* URLs que no son accesibles desde ninguna otra clase */
    public static final String INSERTAR_PASSWORD_URL = "/api/contrasenya/insertar";
    public static final String ELIMINAR_PASSWORD_URL = "/api/constrasenya/eliminar";
    public static final String LISTAR_PASSWORDS_USUARIO_URL = "/api/contrasenya/listar";

    @Autowired
    IPassRepo repoPass;

    /*
    @PostMapping(INSERTAR_PASSWORD_URL)
    public ResponseEntity<JSONObject> insertar(HttpServletRequest request,
                                               @RequestBody InsertPasswordRequest passReq)
            throws UserNotFoundException {

        User usuario = getUserFromRequest(request);
        JSONObject res = new JSONObject();

        // 2 contraseñas con el mismo nombre?¿

    } */

}
