package com.Backend.controller;

import com.Backend.exception.CategoryNotFoundException;
import com.Backend.exception.UserNotFoundException;
import com.Backend.model.Category;
import com.Backend.model.User;
import com.Backend.model.request.DeleteByIdRequest;
import com.Backend.model.request.InsertCategoryRequest;
import com.Backend.repository.ICatRepo;
import com.Backend.repository.IUserRepo;
import com.Backend.utils.CategoryUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.Backend.utils.JsonUtils.peticionCorrecta;
import static com.Backend.utils.JsonUtils.peticionErronea;
import static com.Backend.utils.TokenUtils.getUserIdFromRequest;

@RestController
public class CategoryController {

    /* URLs que no son accesibles desde ninguna otra clase */
    public static final String INSERTAR_CATEGORIA_URL = "/api/categorias/insertar";
    public static final String ELIMINAR_CATEGORIA_URL = "/api/categorias/eliminar";
    public static final String LISTAR_CATEGORIAS_USUARIO_URL = "/api/categorias/listar";

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
                                               @RequestBody InsertCategoryRequest idcr)
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
    public ResponseEntity<JSONObject> eliminar(@RequestBody DeleteByIdRequest del,
                                               HttpServletRequest request) throws UserNotFoundException, CategoryNotFoundException {

        User usuario = getUserFromRequest(request);

        try {
            Category cat = repoCat.findById(del.getId()).orElseThrow(() -> new CategoryNotFoundException(del.getId()));
            if (cat.getUsuario().getId().equals(usuario.getId())) {
                repoCat.deleteById(cat.getId());
                return peticionCorrecta();
            } else
                return peticionErronea("La categoría " + del.getId() + " pertenece a otro usuario.");
        }catch(CategoryNotFoundException c){
            return peticionErronea(c.getMessage());
        }
    }

    @GetMapping(LISTAR_CATEGORIAS_USUARIO_URL)
    public ResponseEntity<JSONObject> listarEditables(HttpServletRequest request)
            throws UserNotFoundException {
        User usuario = getUserFromRequest(request);
        List<Category> categorias = repoCat.findByUsuario(usuario);

        JSONArray jsa = CategoryUtils.arrayCategorias(categorias, true);

        JSONObject res = new JSONObject();
        res.put("categories", jsa);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


}
