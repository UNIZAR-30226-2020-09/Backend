package com.Backend.controller;

import com.Backend.exception.UserNotFoundException;
import com.Backend.model.Category;
import com.Backend.model.User;
import com.Backend.model.request.InsertDeleteCategoryRequest;
import com.Backend.model.response.CategoryResponse;
import com.Backend.repository.ICatRepo;
import com.Backend.repository.IUserRepo;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.Backend.utils.TokenUtils.getUserIdFromRequest;

@RestController
public class CatController {

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

    /*@PostMapping(INSERTAR_CATEGORIA_URL)
    public ResponseEntity<JSONObject> insertar(HttpServletRequest request,
                                               @RequestBody InsertDeleteCategoryRequest idcr) throws UserNotFoundException {

        Long id = getUserIdFromRequest(request);
        User usuario = repoUser.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        JSONObject res = new JSONObject();

        Boolean existsCat = repoCat.existsByUsuarioAndCategoryName(usuario, idcr.getName());

        if (!existsCat) {
            repoCat.save(new Category(idcr.getName(), usuario));
            res.put("statusText", "Categoría creada correctamente");
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } else {
            res.put("statusText", "Ya existe una categoría con ese nombre");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    @DeleteMapping(ELIMINAR_CATEGORIA_URL)
    public ResponseEntity<JSONObject> eliminar(@RequestBody InsertDeleteCategoryRequest idcr,
                                               HttpServletRequest request) throws UserNotFoundException {

        Long id = getUserIdFromRequest(request);
        User usuario = repoUser.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        JSONObject res = new JSONObject();

        Boolean existsCat = repoCat.existsByUsuarioAndCategoryName(usuario, idcr.getName());

        if (existsCat) {
            Category cat = repoCat.findByUsuarioAndCategoryName(usuario, idcr.getName());
            repoCat.deleteById(cat.getId());
            res.put("statusText", "La categoría " + idcr.getName() + " ha sido eliminada correctamente.");
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } else {
            res.put("statusText", "La categoría " + idcr.getName() + " no existe.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }

    // MUY PROBABLE QUE HAYA QUE CAMBIAR EL FORMATO
    @GetMapping(LISTAR_CATEGORIAS_USUARIO_URL)
    public List<CategoryResponse> listar(HttpServletRequest request)
            throws UserNotFoundException {

        Long id = getUserIdFromRequest(request);
        User usuario = repoUser.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        List<Category> categorias = repoCat.findByUsuario(usuario);
        List<CategoryResponse> response = new ArrayList<>();
        for (Category cat : categorias)
            response.add(new CategoryResponse(cat));

        return response;
    }*/


}
