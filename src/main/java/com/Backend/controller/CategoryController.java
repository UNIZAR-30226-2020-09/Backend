package com.Backend.controller;

import com.Backend.exception.CategoryNotFoundException;
import com.Backend.exception.UserNotFoundException;
import com.Backend.model.Category;
import com.Backend.model.Password;
import com.Backend.model.User;
import com.Backend.model.request.category.InsertCategoryRequest;
import com.Backend.model.request.category.ModifyCategoryRequest;
import com.Backend.repository.ICatRepo;
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

import static com.Backend.utils.JsonUtils.peticionCorrecta;
import static com.Backend.utils.JsonUtils.peticionErronea;
import static com.Backend.utils.TokenUtils.getUserFromRequest;

@RestController
public class CategoryController {

    /* URLs que no son accesibles desde ninguna otra clase */
    public static final String INSERTAR_CATEGORIA_URL = "/api/categorias/insertar";
    public static final String ELIMINAR_CATEGORIA_URL = "/api/categorias/eliminar";
    public static final String LISTAR_CATEGORIAS_USUARIO_URL = "/api/categorias/listar";
    public static final String MODIFICAR_CATEGORIAS_USUARIO_URL = "/api/categorias/modificar";

    @Autowired
    ICatRepo repoCat;

    @Autowired
    IUserRepo repoUser;

    @Autowired
    IPassRepo repoPass;

    @PostMapping(INSERTAR_CATEGORIA_URL)
    public ResponseEntity<JSONObject> insertar(HttpServletRequest request,
                                               @RequestBody InsertCategoryRequest idcr){
        try {
            User usuario = getUserFromRequest(request, repoUser);
            if (!idcr.isValid())
                return peticionErronea("Los campos no pueden quedar vacíos.");

            Boolean existsCat = repoCat.existsByUsuarioAndCategoryName(usuario, idcr.getCategoryName());
            if (existsCat) {
                return peticionErronea("Ya existe una categoría con ese nombre para el usuario.");
            }
            repoCat.save(new Category(idcr.getCategoryName(), usuario));
            return peticionCorrecta();
        }
        catch(UserNotFoundException e){
            return peticionErronea("El usuario no existe.");
        }
    }

    @DeleteMapping(ELIMINAR_CATEGORIA_URL)
    public ResponseEntity<JSONObject> eliminar(@RequestParam Long id,
                                               HttpServletRequest request) throws UserNotFoundException {

        User usuario = getUserFromRequest(request, repoUser);
        try {
            Category cat = repoCat.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
            if (cat.getUsuario().getId().equals(usuario.getId()) && !cat.equals(getSinCategoria(repoCat, usuario))) {
                modifyPasswordsAtCategoryDelete(cat, repoPass, repoCat, cat.getUsuario());
                repoCat.deleteById(cat.getId());
                return peticionCorrecta();
            } else
                return peticionErronea("No se ha podido eliminar la categoría.");
        }catch(CategoryNotFoundException c){
            return peticionErronea(c.getMessage());
        }
    }

    @GetMapping(LISTAR_CATEGORIAS_USUARIO_URL)
    public ResponseEntity<JSONObject> listarEditables(HttpServletRequest request)
            throws UserNotFoundException {
        User usuario = getUserFromRequest(request, repoUser);
        List<Category> categorias = repoCat.findByUsuario(usuario);

        JSONArray jsa = arrayCategorias(categorias);

        JSONObject res = new JSONObject();
        res.put("categories", jsa);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping(MODIFICAR_CATEGORIAS_USUARIO_URL)
    public ResponseEntity<JSONObject> modificar(@RequestBody ModifyCategoryRequest modCat,
                                                HttpServletRequest request) throws UserNotFoundException {

        User usuario = getUserFromRequest(request, repoUser);
        if(modCat.isValid()) {
            Category cat = repoCat.findByUsuarioAndId(usuario, modCat.getId());
            if(cat.equals(getSinCategoria(repoCat,usuario))) {
                return peticionErronea("No se permite modificar esa categoría.");
            }
            else {
                Boolean existsCat = repoCat.existsByUsuarioAndCategoryName(usuario, modCat.getCategoryName());
                if (existsCat) {
                    return peticionErronea("Ya existe una categoría con ese nombre para el usuario.");
                }
                cat.setCategoryName(modCat.getCategoryName());
                repoCat.save(cat);
                return peticionCorrecta();
            }
        } else
            return peticionErronea("No se permiten campos nulos.");
    }

    public void modifyPasswordsAtCategoryDelete(Category cat, IPassRepo repoPass,
                                                       ICatRepo repoCat, User usuario){
        List<Password> passwords = repoPass.findByCategory(cat);
        Category sinCat = getSinCategoria(repoCat,usuario);

        for(Password pass : passwords){
            pass.setCategory(sinCat);
            repoPass.save(pass);
        }
    }

    public static JSONArray arrayCategorias(List<Category> categorias){
        JSONArray jsa = new JSONArray();
        for (Category cat : categorias) {
            JSONObject obj = new JSONObject();
            obj.put("catId", cat.getId());
            obj.put("categoryName", cat.getCategoryName());
            jsa.add(obj);
        }
        return jsa;
    }

    //Segunda en crearse al crear usuario, segunda con menor id
    public static Category getSinCategoria(ICatRepo repoCat, User usuario) {
        List<Category> categorias = repoCat.findByUsuarioOrderByIdAsc(usuario);
        return categorias.get(1);
    }

}
