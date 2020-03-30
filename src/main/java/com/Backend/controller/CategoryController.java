package com.Backend.controller;

import com.Backend.exception.UserNotFoundException;
import com.Backend.model.Category;
import com.Backend.model.User;
import com.Backend.model.request.InsertDeleteCategoryRequest;
import com.Backend.model.response.CategoryResponse;
import com.Backend.repository.ICatRepo;
import com.Backend.repository.IUserRepo;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
//import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.Backend.utils.TokenUtils.getUserIdFromRequest;

@RestController
public class CategoryController {

    /* URLs que no son accesibles desde ninguna otra clase */
    public static final String INSERTAR_CATEGORIA_URL = "/api/categorias/insertar";
    public static final String ELIMINAR_CATEGORIA_URL = "/api/categorias/eliminar";
    public static final String LISTAR_CATEGORIAS_USUARIO_URL = "/api/categorias/listar";

    /*
     * Anota warning "débil", según he leído hacer autowired a un atributo que es una interfaz
     * provoca una implementación en la instanciación de la clase. Inyección de atributos
     */
    @Autowired
    ICatRepo repoCat;

    @Autowired
    IUserRepo repoUser;

    public User getUserFromRequest(HttpServletRequest request) throws UserNotFoundException{
        Long id = getUserIdFromRequest(request);
        User usuario = repoUser.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return usuario;
    }

    @PostMapping(INSERTAR_CATEGORIA_URL)
    public ResponseEntity<JSONObject> insertar(HttpServletRequest request,
                                               @RequestBody InsertDeleteCategoryRequest idcr)
            throws UserNotFoundException{
        JSONObject res = new JSONObject();
        try {
            User usuario = getUserFromRequest(request);
            if (!idcr.isValid()) {
                res.put("statusText", "Los campos no pueden quedar vacíos.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }

            Boolean existsCat = repoCat.existsByUsuarioAndCategoryName(usuario, idcr.getCategoryName());
            if (existsCat) {
                res.put("statusText", "Ya existe una categoría con ese nombre");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }
            repoCat.save(new Category(idcr.getCategoryName(), usuario));
            res.put("statusText", "Categoría creada correctamente");
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        catch(UserNotFoundException e){
            res.put("statusText", "Usuario no existente");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    @DeleteMapping(ELIMINAR_CATEGORIA_URL)
    public ResponseEntity<JSONObject> eliminar(@RequestBody InsertDeleteCategoryRequest idcr,
                                               HttpServletRequest request) throws UserNotFoundException {

        JSONObject res = new JSONObject();
        try {
            User usuario = getUserFromRequest(request);
            if (!idcr.isValid()) {
                res.put("statusText", "Los campos no pueden quedar vacíos.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }
            Boolean existsCat = repoCat.existsByUsuarioAndCategoryName(usuario, idcr.getCategoryName());
            if (!existsCat) {
                res.put("statusText", "La categoría " + idcr.getCategoryName() + " no existe.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }
            Category cat = repoCat.findByUsuarioAndCategoryName(usuario, idcr.getCategoryName());
            repoCat.deleteById(cat.getId());
            res.put("statusText", "La categoría " + idcr.getCategoryName() + " ha sido eliminada correctamente.");
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        catch(UserNotFoundException e){
            res.put("statusText", "Usuario no existente");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    @GetMapping(LISTAR_CATEGORIAS_USUARIO_URL)
    public ResponseEntity<JSONObject> listar(HttpServletRequest request)
            throws UserNotFoundException {

        JSONObject res = new JSONObject();
        try {
            User usuario = getUserFromRequest(request);
            List<Category> categorias = repoCat.findByUsuario(usuario);

            JSONArray array = new JSONArray();
            for (Category cat : categorias)
                array.add(new CategoryResponse(cat));

            res.put("categories", array);
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        catch(UserNotFoundException e){
            res.put("statusText", "Usuario no existente");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }


}
