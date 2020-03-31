package com.Backend.controller;

import com.Backend.exception.CategoryNotFoundException;
import com.Backend.exception.UserNotFoundException;
import com.Backend.model.Category;
import com.Backend.model.User;
import com.Backend.model.request.DeleteByIdRequest;
import com.Backend.model.request.InsertCategoryRequest;
import com.Backend.repository.ICatRepo;
import com.Backend.repository.IUserRepo;
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
                                               @RequestBody InsertCategoryRequest idcr)
            throws UserNotFoundException{

        User usuario = getUserFromRequest(request);
        JSONObject res = new JSONObject();

        Boolean existsCat = repoCat.existsByUsuarioAndCategoryName(usuario, idcr.getCategoryName());

        if (!existsCat) {
            repoCat.save(new Category(idcr.getCategoryName(), usuario));
            res.put("statusText", "Categoría creada correctamente");
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } else {
            res.put("statusText", "Ya existe una categoría con ese nombre");
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
    public ResponseEntity<JSONObject> listar(HttpServletRequest request)
            throws UserNotFoundException {

        User usuario = getUserFromRequest(request);
        List<Category> categorias = repoCat.findByUsuario(usuario);

        JSONArray jsa = new JSONArray();

        for (Category cat : categorias) {
            JSONObject obj = new JSONObject();
            obj.put("catId", cat.getId());
            obj.put("categoryName", cat.getCategoryName());
            jsa.add(obj);
        }

        JSONObject res = new JSONObject();

        System.out.println(res.toString());
        res.put("categories", jsa);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


}
